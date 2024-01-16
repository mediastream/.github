//
//  AudioViewController.swift
//  MediastreamSampleApp
//
//  Created by Carlos Ruiz on 16-01-24.
//

import UIKit
import MediastreamPlatformSDKiOS

class AudioViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()        
    }
    
    @IBAction func ShowAOD(_ sender: Any) {
        let storyboard = self.storyboard?.instantiateViewController(withIdentifier: "PlayerViewController") as! PlayerViewController
        let _msConfig = MediastreamPlayerConfig()
        _msConfig.id = "CONTENT_ID"
        _msConfig.accountID = "ACCOUNT_ID"
        _msConfig.videoFormat = MediastreamPlayerConfig.AudioVideoFormat.MP3
        _msConfig.type = MediastreamPlayerConfig.VideoTypes.VOD
        storyboard.msConfig = _msConfig
        self.navigationController?.pushViewController(storyboard, animated: true)
    }
    
    @IBAction func ShowLive(_ sender: Any) {
        let storyboard = self.storyboard?.instantiateViewController(withIdentifier: "PlayerViewController") as! PlayerViewController
        let _msConfig = MediastreamPlayerConfig()
        _msConfig.id = "CONTENT_ID"
        _msConfig.accountID = "ACCOUNT_ID"
        _msConfig.type = MediastreamPlayerConfig.VideoTypes.LIVE
        storyboard.msConfig = _msConfig
        self.navigationController?.pushViewController(storyboard, animated: true)
    }
}
