import SwiftUI
import UIKit
import ComposeApp

struct ComposeContainerView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        controller.view.isUserInteractionEnabled = true
        controller.view.isMultipleTouchEnabled = true
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeContainerView()
            .ignoresSafeArea()
    }
}
