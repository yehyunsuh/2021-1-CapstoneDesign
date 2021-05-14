using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ARDrawLine : MonoBehaviour
{
    public Transform _pivotPoint;
    public GameObject _lineRenderePrefabs;
    private LineRenderer _lineRendere;
    public List<LineRenderer> _lineList = new List<LineRenderer>();
    public Transform _linePool;
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
