//
//  ViewController7.swift
//  ARKit_App
//
//  Created by 저스트비버 on 2021/05/31.
//

import UIKit
import RealityKit
import ARKit

class ViewController7: UIViewController {
    
    @IBOutlet weak var arView: ARView!
    
    var public_distance:Int = 0
    var product_distance:Int = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("ViewController7")
        print(public_distance)
//        if (public_distance > 30) {
//            showAlertAble()
//        } else {
//            showAlertUnAble()
//        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        arView.session.delegate = self

        setupARView()

        arView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(recognizer:))))
    }

    //MARK: Setup Methods
    func setupARView() {
        arView.automaticallyConfigureSession = false
        let configuration = ARWorldTrackingConfiguration()
        configuration.planeDetection = [.horizontal, .vertical]
        configuration.environmentTexturing = .automatic
        arView.session.run(configuration)
    }

    //MARK: Object Placement
    @objc
    func handleTap(recognizer: UITapGestureRecognizer) {
        let location = recognizer.location(in: arView)

        let results = arView.raycast(from: location, allowing: .estimatedPlane, alignment: .horizontal)
        
//        let transform_array_value = makeScaleMatrix(xScale:0.3, yScale:0.3, zScale:0.3)
//        print(transform_array_value)
        
        if let firstResult = results.first {
            let anchor = ARAnchor(name: "blue", transform: firstResult.worldTransform)
            //print(anchor)
            //let anchor = ARAnchor(name: "blue", transform: firstResult.worldTransform)
            arView.session.add(anchor: anchor)
        } else {
            print("Object placement failed -couldn't find surface.")
        }
    }

    func placeObject(named entityName: String, for anchor: ARAnchor) {
        let entity = try! ModelEntity.loadModel(named: entityName)

        entity.generateCollisionShapes(recursive: true)
        arView.installGestures([.rotation,.translation], for: entity)

        let anchorEntity = AnchorEntity(anchor: anchor)
        anchorEntity.addChild(entity)
        arView.scene.addAnchor(anchorEntity)
    }
    
//    func showAlertAble() {
//        let alert = UIAlertController(title: "", message: "배치가 가능합니다", preferredStyle: .alert)
//        alert.addAction(UIAlertAction(title: "확인", style: .cancel, handler: { action in
//            print("tapped dismiss")
//        }))
//        present(alert, animated: true)
//    }
//    func showAlertUnAble() {
//        let alert = UIAlertController(title: "", message: "배치가 불가능합니다. 다른 장소를 선택하세요. (가구 길이: 30cm)", preferredStyle: .alert)
//        alert.addAction(UIAlertAction(title: "확인", style: .cancel, handler: { action in
//            print("tapped dismiss")
//        }))
//        present(alert, animated: true)
//    }
    
//    func makeScaleMatrix(xScale: Float, yScale: Float, zScale: Float) -> simd_float4x4 {
//        let rows = [
//            simd_float4(xScale,      0,     0,      0),
//            simd_float4(     0, yScale,     0,      0),
//            simd_float4(     0,      0, zScale,     0),
//            simd_float4(     0,      0,     0,      1)
//        ]
//
//        return float4x4(rows: rows)
//    }
}

extension ViewController7: ARSessionDelegate {
    func session(_ session: ARSession, didAdd anchors: [ARAnchor]) {
        for anchor in anchors {
            if let anchorName = anchor.name, anchorName == "blue" {
                placeObject(named: anchorName, for: anchor)
            }
        }
    }
}
