using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class OnPress : MonoBehaviour, IPointerDownHandler, IPointerUpHandler
{
    // Start is called before the first frame update
    bool _pressed = false;
    public void onPointerDown(PointerEventData eventData)
    {
        _pressed = true;
    }

    public void onPointerUp(PointerEventData eventData)
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
