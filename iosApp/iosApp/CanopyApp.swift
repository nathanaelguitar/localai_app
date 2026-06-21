import SwiftUI
import UIKit
import ComposeApp

@main
struct CanopyApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea()
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        controller.view.isUserInteractionEnabled = true
        controller.view.isMultipleTouchEnabled = true
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
