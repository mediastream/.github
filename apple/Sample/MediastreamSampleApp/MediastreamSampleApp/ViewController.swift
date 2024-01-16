//
//  ViewController.swift
//  MediastreamSampleApp
//
//  Created by Carlos Ruiz on 15-01-24.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }

    @IBAction func ShowVideo(_ sender: Any) {
        let storyboard = self.storyboard?.instantiateViewController(withIdentifier: "VideoViewController") as! VideoViewController
        self.navigationController?.pushViewController(storyboard, animated: true)

    }
    
    @IBAction func ShowAudio(_ sender: Any) {
        let storyboard = self.storyboard?.instantiateViewController(withIdentifier: "AudioViewController") as! AudioViewController
        self.navigationController?.pushViewController(storyboard, animated: true)
    }
}

