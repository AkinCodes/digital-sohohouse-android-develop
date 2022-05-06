/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sohohouse.seven.common.adapterhelpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sohohouse.seven.common.views.ExpandableListView
import com.sohohouse.seven.home.houseboard.viewholders.NotificationViewHolder
import java.util.*

/**
 * A copy of Android DefaultItemAnimator and modified to meet the needs for  Notification Centre
 * This implements animations for collapsing and expanding the item list
 */
class StackItemAnimator(
    private var expanded: Boolean = false,
    private val stackOffsetFactor: Float
) : SimpleItemAnimator(), ExpandableListView.Listener {

    private val mPendingRemovals = ArrayList<RecyclerView.ViewHolder>()
    private val mPendingAdditions = ArrayList<RecyclerView.ViewHolder>()
    private val mPendingMoves = ArrayList<MoveInfo>()
    private val mPendingChanges = ArrayList<ChangeInfo>()

    private val mAdditionsList = ArrayList<ArrayList<RecyclerView.ViewHolder>>()
    private val mMovesList = ArrayList<ArrayList<MoveInfo>>()
    private val mChangesList = ArrayList<ArrayList<ChangeInfo>>()

    private val mAddAnimations = ArrayList<RecyclerView.ViewHolder>()
    private val mMoveAnimations = ArrayList<RecyclerView.ViewHolder>()
    private val mRemoveAnimations = ArrayList<RecyclerView.ViewHolder>()
    private val mChangeAnimations = ArrayList<RecyclerView.ViewHolder>()

    var stackPosition: Int = 0

    private class MoveInfo(
        var holder: RecyclerView.ViewHolder,
        var fromX: Int,
        var fromY: Int,
        var toX: Int,
        var toY: Int
    )

    private class ChangeInfo private constructor(
        var oldHolder: RecyclerView.ViewHolder?,
        var newHolder: RecyclerView.ViewHolder?
    ) {
        var fromX: Int = 0
        var fromY: Int = 0
        var toX: Int = 0
        var toY: Int = 0

        constructor(
            oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder,
            fromX: Int, fromY: Int, toX: Int, toY: Int
        ) : this(oldHolder, newHolder) {
            this.fromX = fromX
            this.fromY = fromY
            this.toX = toX
            this.toY = toY
        }

        override fun toString(): String {
            return ("ChangeInfo{"
                    + "oldHolder=" + oldHolder
                    + ", newHolder=" + newHolder
                    + ", fromX=" + fromX
                    + ", fromY=" + fromY
                    + ", toX=" + toX
                    + ", toY=" + toY
                    + '}'.toString())
        }
    }

    override fun onExpandableListChanged(expanded: Boolean) {
        this.expanded = expanded
    }

    override fun runPendingAnimations() {
        val removalsPending = mPendingRemovals.isNotEmpty()
        val movesPending = mPendingMoves.isNotEmpty()
        val changesPending = mPendingChanges.isNotEmpty()
        val additionsPending = mPendingAdditions.isNotEmpty()
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            // nothing to animate
            return
        }
        // First, remove stuff
        for (holder in mPendingRemovals) {
            animateRemoveImpl(holder)
        }
        mPendingRemovals.clear()
        // Next, move stuff
        if (movesPending) {
            val moves = ArrayList(mPendingMoves)
            mMovesList.add(moves)
            mPendingMoves.clear()
            val mover = Runnable {
                for (moveInfo in moves) {
                    animateMoveImpl(
                        moveInfo.holder, moveInfo.fromX, moveInfo.fromY,
                        moveInfo.toX, moveInfo.toY
                    )
                }
                moves.clear()
                mMovesList.remove(moves)
            }
            if (removalsPending) {
                val view = moves[0].holder.itemView
                ViewCompat.postOnAnimation(view, mover)
            } else {
                mover.run()
            }
        }
        // Next, change stuff, to run in parallel with move animations
        if (changesPending) {
            val changes = ArrayList(mPendingChanges)
            mChangesList.add(changes)
            mPendingChanges.clear()
            val changer = Runnable {
                for (change in changes) {
                    animateChangeImpl(change)
                }
                changes.clear()
                mChangesList.remove(changes)
            }
            if (removalsPending) {
                val holder = changes[0].oldHolder
                ViewCompat.postOnAnimation(holder!!.itemView, changer)
            } else {
                changer.run()
            }
        }
        // Next, add stuff
        if (additionsPending) {
            val additions = ArrayList<RecyclerView.ViewHolder>(mPendingAdditions)
            mAdditionsList.add(additions)
            mPendingAdditions.clear()
            val adder = Runnable {
                for (holder in additions) {
                    animateAddImpl(holder)
                }
                additions.clear()
                mAdditionsList.remove(additions)
            }
            if (removalsPending || movesPending || changesPending) {
                val view = additions[0].itemView
                ViewCompat.postOnAnimation(view, adder)
            } else {
                adder.run()
            }
        }
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        resetAnimation(holder)
        mPendingRemovals.add(holder)
        return true
    }

    private fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        val animation = view.animate()
        mRemoveAnimations.add(holder)
        animation.setDuration(removeDuration).alpha(0f).setListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    dispatchRemoveStarting(holder)
                }

                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    view.alpha = 1f
                    dispatchRemoveFinished(holder)
                    mRemoveAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        resetAnimation(holder)
        if (holder is NotificationViewHolder) {
            when {
                holder.adapterPosition in (stackPosition + 1)..(stackPosition + 2) -> {
                    val translationY = holder.itemView.height.toFloat() * holder.adapterPosition
                    holder.itemView.translationY = if (expanded) -translationY else translationY

                    val scale =
                        if (!expanded) 1f - stackOffsetFactor * holder.adapterPosition else 1f
                    holder.itemView.scaleX = scale
                    holder.itemView.scaleY = scale

                    holder.itemView.alpha = 1f
                }
                holder.adapterPosition > (stackPosition + 2) -> {
                    holder.itemView.alpha = if (expanded) 0f else 1f
                }
            }
        } else {
            resetAnimation(holder)
            holder.itemView.alpha = 0f
        }
        mPendingAdditions.add(holder)
        return true
    }

    private fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        val animation = view.animate()
        mAddAnimations.add(holder)

        if (holder is NotificationViewHolder) {
            when {
                holder.adapterPosition in (stackPosition + 1)..(stackPosition + 2) -> {
                    val scale =
                        if (!expanded) 1f - stackOffsetFactor * holder.adapterPosition else 1f
                    animation.scaleX(scale)
                    animation.scaleY(scale)
                    animation.translationY(0f)
                }
                holder.adapterPosition > (stackPosition + 2) -> animation.alpha(if (expanded) 1f else 0f)
            }
        } else {
            animation.alpha(1f)
        }

        animation.setDuration(addDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    dispatchAddStarting(holder)
                }

                override fun onAnimationEnd(animator: Animator) {
                    view.alpha = 1f
                    animation.setListener(null)
                    dispatchAddFinished(holder)
                    mAddAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder, fromX: Int, fromY: Int,
        toX: Int, toY: Int
    ): Boolean {
        val view = holder.itemView
        val newFromX = fromX + holder.itemView.translationX.toInt()
        val newFromY = fromY + holder.itemView.translationY.toInt()
        resetAnimation(holder)
        val deltaX = toX - newFromX
        val deltaY = toY - newFromY
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) {
            view.translationX = (-deltaX).toFloat()
        }
        if (deltaY != 0) {
            view.translationY = (-deltaY).toFloat()
        }
        mPendingMoves.add(MoveInfo(holder, newFromX, newFromY, toX, toY))
        return true
    }

    private fun animateMoveImpl(
        holder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ) {
        val view = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) {
            view.animate().translationX(0f)
        }
        if (deltaY != 0) {
            view.animate().translationY(0f)
        }
        // TODO: make EndActions end listeners instead, since end actions aren't called when
        // vpas are canceled (and can't end them. why?)
        // need listener functionality in VPACompat for this. Ick.
        val animation = view.animate()
        mMoveAnimations.add(holder)
        animation.setDuration(moveDuration).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animator: Animator) {
                dispatchMoveStarting(holder)
            }

            override fun onAnimationCancel(animator: Animator) {
                if (deltaX != 0) {
                    view.translationX = 0f
                }
                if (deltaY != 0) {
                    view.translationY = 0f
                }
            }

            override fun onAnimationEnd(animator: Animator) {
                animation.setListener(null)
                dispatchMoveFinished(holder)
                mMoveAnimations.remove(holder)
                dispatchFinishedWhenDone()
            }
        }).start()
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder,
        fromX: Int, fromY: Int, toX: Int, toY: Int
    ): Boolean {
        if (oldHolder === newHolder) {
            // Don't know how to run change animations when the same view holder is re-used.
            // run a move animation to handle position changes.
            return animateMove(oldHolder, fromX, fromY, toX, toY)
        }
        val prevTranslationX = oldHolder.itemView.translationX
        val prevTranslationY = oldHolder.itemView.translationY
        val prevAlpha = oldHolder.itemView.alpha
        resetAnimation(oldHolder)
        val deltaX = (toX.toFloat() - fromX.toFloat() - prevTranslationX).toInt()
        val deltaY = (toY.toFloat() - fromY.toFloat() - prevTranslationY).toInt()
        // recover prev translation state after ending animation
        oldHolder.itemView.translationX = prevTranslationX
        oldHolder.itemView.translationY = prevTranslationY
        oldHolder.itemView.alpha = prevAlpha
        // carry over translation values
        resetAnimation(newHolder)
        newHolder.itemView.translationX = (-deltaX).toFloat()
        newHolder.itemView.translationY = (-deltaY).toFloat()
        newHolder.itemView.alpha = 1f

        if (oldHolder is NotificationViewHolder) {
            val scale = if (expanded) 1f - stackOffsetFactor * newHolder.adapterPosition else 1f
            oldHolder.itemView.scaleX = scale
            oldHolder.itemView.scaleY = scale
            newHolder.itemView.scaleX = scale
            newHolder.itemView.scaleY = scale
        }

        mPendingChanges.add(ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY))
        return true
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val oldHolder = changeInfo.oldHolder
        val newHolder = changeInfo.newHolder
        val position = newHolder?.adapterPosition ?: stackPosition

        oldHolder?.itemView?.let { view ->
            val oldViewAnim = view.animate().setDuration(changeDuration)
            mChangeAnimations.add(oldHolder)

            if (oldHolder is NotificationViewHolder) {
                val scale = if (expanded) 1f else 1f - stackOffsetFactor * position
                oldViewAnim.scaleX(scale)
                oldViewAnim.scaleY(scale)
            }

            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat())
            oldViewAnim.translationY((changeInfo.toY - changeInfo.fromY).toFloat())
            oldViewAnim.alpha(0f).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    dispatchChangeStarting(changeInfo.oldHolder, true)
                }

                override fun onAnimationEnd(animator: Animator) {
                    oldViewAnim.setListener(null)
                    view.alpha = 1f
                    view.translationX = 0f
                    view.translationY = 0f
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                    mChangeAnimations.remove(oldHolder)
                    dispatchFinishedWhenDone()
                }
            }).start()
        }

        newHolder?.itemView?.let { view ->
            val newViewAnimation = view.animate()
            mChangeAnimations.add(newHolder)

            if (newHolder is NotificationViewHolder) {
                if (oldHolder is NotificationViewHolder) {
                    val scale = if (expanded) 1f else 1f - stackOffsetFactor * position
                    newViewAnimation.scaleX(scale)
                    newViewAnimation.scaleY(scale)
                }
            }

            newViewAnimation.translationX(0f).translationY(0f).setDuration(changeDuration)
                .alpha(1f).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animator: Animator) {
                        dispatchChangeStarting(changeInfo.newHolder, false)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        newViewAnimation.setListener(null)
                        view.alpha = 1f
                        view.translationX = 0f
                        view.translationY = 0f
                        dispatchChangeFinished(changeInfo.newHolder, false)
                        mChangeAnimations.remove(newHolder)
                        dispatchFinishedWhenDone()
                    }
                }).start()
        }
    }

    private fun endChangeAnimation(
        infoList: MutableList<ChangeInfo>,
        item: RecyclerView.ViewHolder
    ) {
        for (i in infoList.indices.reversed()) {
            val changeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        changeInfo.oldHolder?.let { endChangeAnimationIfNecessary(changeInfo, it) }
        changeInfo.newHolder?.let { endChangeAnimationIfNecessary(changeInfo, it) }
    }

    private fun endChangeAnimationIfNecessary(
        changeInfo: ChangeInfo,
        item: RecyclerView.ViewHolder
    ): Boolean {
        var oldItem = false
        if (changeInfo.newHolder === item) {
            changeInfo.newHolder = null
        } else if (changeInfo.oldHolder === item) {
            changeInfo.oldHolder = null
            oldItem = true
        } else {
            return false
        }
        item.itemView.alpha = 1f
        item.itemView.translationX = 0f
        item.itemView.translationY = 0f
        dispatchChangeFinished(item, oldItem)
        return true
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        val view = item.itemView
        // this will trigger end callback which should set properties to their target values.
        view.animate().cancel()
        // TODO if some other animations are chained to end, how do we cancel them as well?
        for (i in mPendingMoves.indices.reversed()) {
            val moveInfo = mPendingMoves[i]
            if (moveInfo.holder === item) {
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(item)
                mPendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(mPendingChanges, item)
        if (mPendingRemovals.remove(item)) {
            view.alpha = 1f
            dispatchRemoveFinished(item)
        }
        if (mPendingAdditions.remove(item)) {
            view.alpha = 1f
            dispatchAddFinished(item)
        }

        for (i in mChangesList.indices.reversed()) {
            val changes = mChangesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) {
                mChangesList.removeAt(i)
            }
        }
        for (i in mMovesList.indices.reversed()) {
            val moves = mMovesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo = moves[j]
                if (moveInfo.holder === item) {
                    view.translationY = 0f
                    view.translationX = 0f
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) {
                        mMovesList.removeAt(i)
                    }
                    break
                }
            }
        }
        for (i in mAdditionsList.indices.reversed()) {
            val additions = mAdditionsList[i]
            if (additions.remove(item)) {
                view.alpha = 1f
                dispatchAddFinished(item)
                if (additions.isEmpty()) {
                    mAdditionsList.removeAt(i)
                }
            }
        }

        // animations should be ended by the cancel above.

        check(!(mRemoveAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mRemoveAnimations list" }


        check(!(mAddAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mAddAnimations list" }


        check(!(mChangeAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mChangeAnimations list" }


        check(!(mMoveAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mMoveAnimations list" }
        dispatchFinishedWhenDone()
    }

    @SuppressLint("Recycle")
    private fun resetAnimation(holder: RecyclerView.ViewHolder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = ValueAnimator().interpolator
        }
        holder.itemView.animate().interpolator = sDefaultInterpolator
        endAnimation(holder)
    }

    override fun isRunning(): Boolean {
        return (mPendingAdditions.isNotEmpty()
                || mPendingChanges.isNotEmpty()
                || mPendingMoves.isNotEmpty()
                || mPendingRemovals.isNotEmpty()
                || mMoveAnimations.isNotEmpty()
                || mRemoveAnimations.isNotEmpty()
                || mAddAnimations.isNotEmpty()
                || mChangeAnimations.isNotEmpty()
                || mMovesList.isNotEmpty()
                || mAdditionsList.isNotEmpty()
                || mChangesList.isNotEmpty())
    }

    /**
     * Check the state of currently pending and running animations. If there are none
     * pending/running, call [.dispatchAnimationsFinished] to notify any
     * listeners.
     */
    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    override fun endAnimations() {
        var count = mPendingMoves.size
        for (i in count - 1 downTo 0) {
            val item = mPendingMoves[i]
            val view = item.holder.itemView
            view.translationY = 0f
            view.translationX = 0f
            dispatchMoveFinished(item.holder)
            mPendingMoves.removeAt(i)
        }
        count = mPendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item = mPendingRemovals[i]
            dispatchRemoveFinished(item)
            mPendingRemovals.removeAt(i)
        }
        count = mPendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item = mPendingAdditions[i]
            item.itemView.alpha = 1f
            dispatchAddFinished(item)
            mPendingAdditions.removeAt(i)
        }
        count = mPendingChanges.size
        for (i in count - 1 downTo 0) {
            endChangeAnimationIfNecessary(mPendingChanges[i])
        }
        mPendingChanges.clear()
        if (!isRunning) {
            return
        }

        var listCount = mMovesList.size
        for (i in listCount - 1 downTo 0) {
            val moves = mMovesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo = moves[j]
                val item = moveInfo.holder
                val view = item.itemView
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) {
                    mMovesList.remove(moves)
                }
            }
        }
        listCount = mAdditionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = mAdditionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                val view = item.itemView
                view.alpha = 1f
                dispatchAddFinished(item)
                additions.removeAt(j)
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions)
                }
            }
        }
        listCount = mChangesList.size
        for (i in listCount - 1 downTo 0) {
            val changes = mChangesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) {
                    mChangesList.remove(changes)
                }
            }
        }

        cancelAll(mRemoveAnimations)
        cancelAll(mMoveAnimations)
        cancelAll(mAddAnimations)
        cancelAll(mChangeAnimations)

        dispatchAnimationsFinished()
    }

    private fun cancelAll(viewHolders: List<RecyclerView.ViewHolder>) {
        for (i in viewHolders.indices.reversed()) {
            viewHolders[i].itemView.animate().cancel()
        }
    }

    /**
     * {@inheritDoc}
     *
     *
     * If the payload list is not empty, DefaultItemAnimator returns `true`.
     * When this is the case:
     *
     *  * If you override [.animateChange], both
     * ViewHolder arguments will be the same instance.
     *
     *  *
     * If you are not overriding [.animateChange],
     * then DefaultItemAnimator will call [.animateMove] and
     * run a move animation instead.
     *
     *
     */
    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ): Boolean {
        return !payloads.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads)
    }

    companion object {
        private val DEBUG = false

        const val STACK_OFFSET_FACTOR = 0.02f

        private var sDefaultInterpolator: TimeInterpolator? = null
    }
}
