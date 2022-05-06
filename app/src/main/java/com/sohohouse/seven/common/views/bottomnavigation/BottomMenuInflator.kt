package com.sohohouse.seven.common.views.bottomnavigation

import android.content.Context
import android.util.AttributeSet
import android.util.Xml
import android.view.InflateException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


class BottomMenuInflator(private val context: Context) {
    private val XML_MENU = "menu"
    private val XML_ITEM = "item"

    private val items: ArrayList<NavigationItem> = ArrayList()

    fun inflate(menuRes: Int): ArrayList<NavigationItem> {
        val parser = context.resources.getLayout(menuRes)
        try {
            parseMenu(parser, Xml.asAttributeSet(parser))
        } catch (e: XmlPullParserException) {
            throw InflateException("Error inflating menu XML", e)
        } catch (e: IOException) {
            throw InflateException("Error inflating menu XML", e)
        } finally {
            parser.close()
            return items
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMenu(parser: XmlPullParser, attrs: AttributeSet) {
        var eventType = parser.eventType
        var tagName: String
        var lookingForEndOfUnknownTag = false
        var unknownTagName: String? = null

        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.name
                if (tagName == XML_MENU) {
                    eventType = parser.next()
                    break
                }
                throw RuntimeException("Expecting menu, got $tagName")
            }
            eventType = parser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)

        var reachedEndOfMenu = false
        loop@ while (!reachedEndOfMenu) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (lookingForEndOfUnknownTag) {
                        break@loop
                    }

                    tagName = parser.name
                    when (tagName) {
                        XML_ITEM -> {
                            items.add(NavigationItem(context, attrs))
                        }
                        XML_MENU -> {
                            parseMenu(parser, attrs)
                        }
                        else -> {
                            lookingForEndOfUnknownTag = true
                            unknownTagName = tagName
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    tagName = parser.getName()
                    when {
                        lookingForEndOfUnknownTag && tagName == unknownTagName -> {
                            lookingForEndOfUnknownTag = false
                            unknownTagName = null
                        }
                        tagName == XML_MENU -> {
                            reachedEndOfMenu = true
                        }
                        tagName == XML_ITEM -> {
                        }
                    }
                }

                XmlPullParser.END_DOCUMENT -> {
                    throw RuntimeException("Unexpected end of document")
                }
            }
            eventType = parser.next()
        }
    }
}