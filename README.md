# Contents

- Onboarding
  - [Build variants](#Build-variants)
  - [Automatic build](#Automatic-build)
  - [Permissions](#Permissions)
  - [How to connect VPN](#How-to-connect-VPN)
  - [KT sessions from Peter](#KT-sessions-from-Peter)
  - [Pull Requests](#Pull-Requests)
- [Formatting](#Formatting)
- [Accounts](#Accounts)
- [Dialogs](#Dialogs)
- [Creating new screens](#Creating-new-screens)
- [Analytics](#Analytics)

## Onboarding

### Build variants

we have debug, qa and release variants. You may try to build debug version to play around.

`debug` - this is where we should do development stuff

`qa` - same as debug but without Leakcanary and minified

`release` - the version that will be posted on Play Store

### Automatic build

we use Bitrise(https://app.bitrise.io/app/e70f09afc2f0d46d#/builds) for automatic builds

`build-qa` - Builds QA and generates APK

`build-qa-daily` - Builds QA daily and generates APK as well as deploys this APK to appetize.io 
emulator(https://appetize.io/app/qhke9ccwhr4gwg8qzbaxymb3m4)

`build-release-app-bundle` - This build generates app's release bundle, deploys it to Play Store 
 on alpha track and it can be downloaded by selected testers

`run-checks` - Runs checks on pull requests of App's Debug version without APK generation

for additional information about workflows steps, please see confluence page:
https://sohohousedev.atlassian.net/wiki/spaces/DEV/pages/2640412726/Android+automatic+build

### Permissions
you may request permissions for
- Firebase crashlitycs - to see your sins
- VPN - Globacl Protect - required to log into accounts on staging. note: vpn url should be `gp-dev.sohohouse.com`
- Bitrise - every time you commit a build will trigger and run tests
- Postman - we dont have swagger :(
- Figma - for the design files and analytic events

### How to connect VPN 
you should request permission for `digital` account(e.g `name.surname@sohohousedigital.com`)

- In Global Protect enter vpn url `gp-dev.sohohouse.com`

  ![global-protect](https://user-images.githubusercontent.com/60490214/148375227-a4935efb-5f19-4ebb-b9f0-94d61fa0b7e1.png)

- Enter your digital account into Google log in page.

  ![google-gl](https://user-images.githubusercontent.com/60490214/148376117-52b23410-3648-46a5-a1f4-5d7e98345307.png)

- After redirecting to sohohouse.okta Authorization page log in with your soho account

  ![okta](https://user-images.githubusercontent.com/60490214/148376249-3dcc8f63-9f65-43d9-87b9-77309c523982.png)

- You`re connected, Happy coding!

  ![gl](https://user-images.githubusercontent.com/60490214/148376386-06469267-6cf2-4539-93d9-bd928019ed8b.png)

_P.S_
If you are having some issues (e.g script errors) while connecting VPN on windows with url
gp-dev.sohohouse.com please, delete your Global protect app and download it
from `http://gp-dev.sohohouse.com`

### KT sessions from Peter

[part 1](https://sohohouse.slack.com/archives/C02G3PNTEER/p1636653369019100) -
[part 2](https://sohohouse.slack.com/archives/C02G3PNTEER/p1636653358018900) -
[part 3](https://sohohouse.slack.com/archives/C02G3PNTEER/p1636653344018700) -
[part 4](https://sohohouse.slack.com/archives/C02G3PNTEER/p1636647485017800)

### Pull Requests
When sending a PR also include ticket ID in the title for example:
`[AB-123] some randome feature`

## Formatting

Use default Kotlin style guide from [here](https://kotlinlang.org/docs/coding-conventions.html).
Suffix screen names with `Fragment`, `BottomSheetFragment` or `Activity` accordingly. Currently we
do not have a linter, but there are plans to add it soon.

## Accounts

### Test accounts for qa and debug

- `everyuk_account@example.com` / `password`

- `kakhi.kiknadze@example.com` / `password`

- `friends_user_uk_1@example.com` / `password`

- [other accounts](https://sohohousedev.atlassian.net/wiki/spaces/EPAM/pages/871759949/Members+Accounts+to+test+on+staging+environment)

### Test accounts for production

- `software-devs@sohohouse.com` / `oqxKFuGc6CYdKkrcXoHCgtcL`

- `Friends.prod2@test.com` / `letmein01`

- [other accounts](https://sohohousedev.atlassian.net/wiki/spaces/DEV/pages/1875312643/Prod+Test+accounts)

## Dialogs

### Error dialog

a simple error dialog can be shown by `ErrorDialogHelper.showGenericErrorDialog`

# Creating new screens
There are few base classes for screens `BaseFragment`, `BaseMVVMFragment`. If you need to show some
static content you should extend `BaseFragment`, if ViewModel is needed then extend
`BaseMVVMFragment`. There is also `BaseMVVMActivity`, `BaseMVVMBottomSheet`, `BaseBottomSheet`.

If you create an Activity also create fragment and show it inside the Activity. This will improve
reusability. 
We are using `Jetpack navigation` for fragments and if Activity contains more then one fragment, we
should use jetpack navigation lib for that as well as safe args if args passing is needed between fragments.
One of main part of navigation lib is `graph.xml` which can be created separately for each activity and
all fragments directions should be written there which are under this Activity (but not limited to use 
same fragment in other activity or in other graph.xml).

#### ViewModel
When creating ViewModels extend `BaseViewModel` and inject required classes. You may also override
`onScreenViewed` and pass current screen name, this is for logging on Firebase. Use 
`ioDispatcher + coroutineExceptionHandler` when making HTTP requests.

#### ViewBinding
Use viewbinding to access views. There is a library called `viewbindingpropertydelegate`, which
helps keep `binding` property non-nullable. But try to avoid having it as a Fragment property,
instead use it as a local variable inside `onViewCreated` callback.

#### Showing loading
`Loadable` interface has `View` and `ViewModelImp` for showing loaders. ViewModelImp adds 2 new
functions `setLoading()` and `setIdle()`, which can be used when sending requests. On the Fragment
side you can use `View` which adds `observeLoadingState()` and with the help of it you can show/hide
the loader.

#### Showing errors
Similar to showing loadings, there is `Errorable.View` and `Errorable.ViewModelImpl`

# Analytics
In case you need to log an event use AnalyticsManager. Add new event in the `Action` enum and use
`AnalyticsManager.logEventAction()`

`UserGlobalId` is accountId and is set by default when calling `AnalyticsManager.logEventAction()`.

# Firebase Crashlytics 
Use `FirebaseCrashlytics.getInstance()` to `log()` or `recordException()`

### Frequent non-fatal events
There are lots of events about `FirebaseRegistrationService.kt – line 60`,
`JsonUtf8Reader.java – line 1144` and `KCallableImpl.kt line 164`. The cause for them seems to be
similar, all of them are related to network requests. Sometimes server returns 403, 401 or 503.

## Useful Extensions

#### ImageView.setImageWithPlaceHolder
Uses Glide to set image, requires Drawable resource id for placeholder. Also callback for image set is competed can be passed.