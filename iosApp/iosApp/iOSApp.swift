import SwiftUI
import FirebaseCore
import FirebaseAuth
import FirebaseAppCheck
import FirebaseMessaging
import GoogleSignIn
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // let providerFactory = YourAppCheckProviderFactory()
        // AppCheck.setAppCheckProviderFactory(providerFactory)
        
        FirebaseApp.configure()
        AppInitializer.shared.onApplicationStart()
        return true
    }
    
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
    }
    
    @MainActor
    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any]
    ) async -> UIBackgroundFetchResult {
        NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
        return .newData
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification
    ) async -> UNNotificationPresentationOptions {
        let userInfo = notification.request.content.userInfo
        
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Print full message.
        print(userInfo)
        
        // Usa le nuove opzioni non deprecate
        return [[.banner, .list, .sound, .badge]]
    }
}

class YourAppCheckProviderFactory: NSObject, AppCheckProviderFactory {
    func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
        if #available(iOS 14.0, *) {
            return AppAttestProvider(app: app)
        } else {
            return DeviceCheckProvider(app: app)
        }
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ComposeView().onOpenURL { url in
                print("Received URL: \(url)")
                // Auth.auth().canHandle(url)  // solo informativo
                GIDSignIn.sharedInstance.handle(url)
                if Auth.auth().canHandle(url) {
                    // L’URL è stato gestito da Firebase Auth
                    return
                }
            }
        }
    }
}
