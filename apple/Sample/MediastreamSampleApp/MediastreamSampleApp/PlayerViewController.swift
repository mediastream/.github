//
//  PlayerViewController.swift
//  MediastreamSampleApp
//
//  Created by Carlos Ruiz on 16-01-24.
//

import UIKit
import MediastreamPlatformSDKiOS

class PlayerViewController: UIViewController {
    
    var msConfig: MediastreamPlayerConfig?
    let player = MediastreamPlatformSDK()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.addChild(player)
        self.view.addSubview(player.view)
        player.setup(msConfig!)
        player.play()
        
        player.events.listenTo(eventName: "play", action: { (information: Any?) in
            if (information != nil) {
                //Do something
            }
        })
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        player.releasePlayer()
    }
}
