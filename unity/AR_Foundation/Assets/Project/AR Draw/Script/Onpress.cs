using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class Onpress : MonoBehaviour, IPointerDownHandler, IPointerUpHandler
{
    // Start is called before the first frame update
    void onPointerDown(PointerEventData eventData)
    {
        _pressed = true;
    }

    void onPointerUp(PointerEventData eventData)
    {
        _pressed = false;
    }

    // Update is called once per frame
    void Update()
    {
        if(_pressed)
        {
            
        }
    }
}
