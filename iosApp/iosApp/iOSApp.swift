import SwiftUI
import FirebaseMessaging
// import composeApp
import GoogleSignIn
import FirebaseCore
import FirebaseAuth

//class AppDelegate: NSObject, UIApplicationDelegate {
//
//  func application(_ application: UIApplication,
//                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
//      FirebaseApp.configure()
//      AppInitializer.shared.onApplicationStart()
//      
//    return true
//  }
//    
//    func application(
//          _ app: UIApplication,
//          open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]
//        ) -> Bool {
//          var handled: Bool
//
//          handled = GIDSignIn.sharedInstance.handle(url)
//          if handled {
//            return true
//          }
//
//          // Handle other custom URL types.
//
//          // If not handled by this app, return false.
//          return false
//        }
//
//    
//}

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL(perform: { url in
                            print("Received URL: \(url)")
                            Auth.auth().canHandle(url)  // <- just for information purposes
                            GIDSignIn.sharedInstance.handle(url)
                        })
        }
    }
}
