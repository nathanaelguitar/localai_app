import SwiftUI
import UIKit
import ComposeApp

struct ComposeContainerView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeContainerView()
            .ignoresSafeArea()
    }
}
