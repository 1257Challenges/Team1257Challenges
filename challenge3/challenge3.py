import math, json
from TheBlueAlliance.Blue_Alliance_API import *

def winCounter(team):
	

def findVictoriesAmount(team1, team2, team3):
	global ccwm1, ccwm2, ccwm3, wins1, wins2, wins3, losses1, losses2, losses3
	ccwm1 = get_statistics(team1)[3]
	ccwm2 = get_statistics(team2)[3]
	ccwm3 = get_statistics(team3)[3]
	wins1 = get_matches(team1, 'qm')[6]
	wins2 = get_matches(team2, 'qm')[6]
	wins3 = get_matches(team3, 'qm')[6]
	losses1 = get_matches(team1, 'qm')[1] - wins1 #may actually be getting total matches in competition (documentation not specific)
	losses2 = get_matches(team2, 'qm')[1] - wins2
	losses3 = get_matches(team3, 'qm')[1] - wins3
	lossesBeingZero = False
	if losses1 != 0:
		wl1 = wins1 / losses1
	else:
		lossesBeingZero = True
	if losses2 != 0:
		wl2 = wins2 / losses2
	else:
		lossesBeingZero = True
	if losses3 != 0:
		wl3 = wins3 / losses3
	else:
		lossesBeingZero = True
	if not lossesBeingZero:
		exponentValue = ccwm1 + 35 * math.log(wl1) + ccwm2 + 35 * math.log(wl2) + ccwm3 + 35 * math.log(wl3)
		return 1.01 ** exponentValue
	else:
	# If any of the losses values are zero, returns zero to signify impossible functions
		return 0
