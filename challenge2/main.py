//To use this import properly, you need to execute this command:
//sudo pip install TheBlueAlliance
from TheBlueAlliance import *

print("Zach is scrub")
event_code = get_events_and_codes(2016, 'Mount Olive')[1]
event = Event('github_user', 'test', '1.0', event_code)
event.get_event_info()
