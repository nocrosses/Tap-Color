/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Tap for Color
 *
 *  Author: nocrosses
 */
definition(
    name: "Tap for Color",
    namespace: "nocrosses",
    author: "nocrosses",
    description: "Change target bulb color on tap",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
	section("When this switch is tapped...") {
		input "master", "capability.switch", title: "Which Switch?", required:true, multiple:false
	}
    section("Rotate color for these bulbs..."){
		input "bulbs", "capability.colorControl", title: "Which Bulbs?", required:true, multiple:true
    }
}

def installed()
{
	subscribe(master, "switch", switchHandler, [filterEvents: false])
    subscribe(bulbs, "switch.off", clearState, [filterEvents: false])
    subscribe(master, "switch.off", clearState, [filterEvents: false])
}

def uninstalled()
{
	unsubscribe()
}

def updated()
{
	unsubscribe()
	subscribe(master, "switch", switchHandler, [filterEvents: false])
    subscribe(bulbs, "switch.off", clearState, [filterEvents: false])
    subscribe(master, "switch.off", clearState, [filterEvents: false])
}

def switchHandler(evt) {
	log.info evt.value
    log.info atomicState.color;
    if(atomicState.color==null)
    {
    log.info "Set default color";
   		atomicState.color = 0;    
    }
    else{
    	changeColor()
    }
 }
 
 def changeColor()
 {
     log.trace "Begin change color";
	// find the currently turned on bulbs
    def onBulbs = bulbs.findAll { switchVal ->
        switchVal == "on" ? true : false
    }
    // Set the bulb color to the next in line
    def colorIndex = state.color ?: 1;   
 
    if(colorIndex > 10)
		colorIndex = 1;  
    
    def hueColor = 0
    def hueSaturation = 100
    def colorTemperature = 0
    log.trace("Default values, hue: {hueColor} saturation {hueSaturation} temp: {colorTemperature}");
    if(colorIndex == 1)//Blue
		hueColor = 70//60
    if(colorIndex == 2)//red
		hueColor = 0
    else if(colorIndex == 3)//Pink
		hueColor = 83
	else if(colorIndex == 4)//Green
		hueColor = 34.58611111111111//30
	else if(colorIndex == 5)//Yellow
		hueColor = 25//16
	else if(colorIndex == 6)//Orange
		hueColor = 10
	else if(colorIndex == 7)//Purple
		hueColor = 77.24444444444444
    else if(colorIndex == 8)
        colorTemperature = 3000
    else if(colorIndex == 9)
		colorTemperature = 4000
    else if(colorIndex == 10)//Daylight
		colorTemperature = 5001
    
   
    if(colorIndex > 7)
    	hueSaturation = 0
    log.trace("Set values, hue: {hueColor} saturation {hueSaturation} temp: {colorTemperature}");   
    
    atomicState.color = colorIndex +1;
    
    log.trace "hue color:" + hueColor;
    log.trace "color temperature:" + colorTemperature;
    
    bulbs*.setColor(hue: hueColor, saturation: hueSaturation);
    
     if(colorTemperature > 0)
     bulbs*.setColorTemperature(colorTemperature);
}

def clearState(evt)
{
	log.trace "State before remove" + atomicState.color
	atomicState.color = null;
    log.trave "State after remove " + atomicState.color
}