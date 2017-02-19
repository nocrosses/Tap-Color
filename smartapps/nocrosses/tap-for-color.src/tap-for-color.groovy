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
	log.info "Event value: $evt.value";
    log.info "State color: $state.color";
    log.info "State reset: $state.reset";
    
    //Check to see if the cycle should be reset
    if(state.reset != 0)
    {
   		log.info "Set default color";
  		state.reset = 0;
   		state.color = 0;    
        log.info "State color: $state.color";
    	log.info "State reset: $state.reset";        
    }
    else{
    log.info "Calling change color";
    	changeColor()
    }
 }
 
 def changeColor()
 {
     log.trace "Begin change color";
 
 	//define the bulb settings to cycle through
 	def colorOptions = [
    	[color: [hue: 70, saturation: 100], temp:0], //blue
        [color: [hue: 77, saturation: 100], temp:0], //purple    
        [color: [hue: 0, saturation: 100], temp:0], //red    
        [color: [hue: 83, saturation: 100], temp:0], //pink    
		[color: [hue: 10, saturation: 100], temp:0], //orange   
        [color: [hue: 25, saturation: 100], temp:0], //yellow 
        [color: [hue: 34.5, saturation: 100], temp:0], //green    
        [color: [hue: 0, saturation: 0], temp:3000], //Warm White
        [color: [hue: 0, saturation: 0], temp:4000], //Cool White 
        [color: [hue: 0, saturation: 0], temp:5001] //Daylight
    ];
    
    log.trace "Color options defined";
	
    // find the currently turned on bulbs
    def onBulbs = bulbs.findAll { switchVal ->
        switchVal == "on" ? true : false
    }
    
    log.trace "Bulbs found: $onBulbs.size()";
    
    // Set the bulb color to the next in line
    def colorIndex = state.color ?: 0;   
    
    log.trace "Current color index: $colorIndex";
    
    log.trace "Number of color options: $colorOptions.size()";
 
    if(colorIndex > colorOptions.size())
		colorIndex = 0;  
        
    def currentColor = colorOptions[colorIndex];
    
    state.color = colorIndex +1;
    
    log.trace "Current color: $currentColor";
    
    bulbs*.setColor(currentColor.color);
    
     if(currentColor.temp > 0)
     bulbs*.setColorTemperature(currentColor.temp);
}

def clearState(evt)
{
	log.trace "State before remove" + state.color
	state.reset = 1;
    log.trace "State after remove " + state.color
}