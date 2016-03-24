import math, json

def findVictoriesAmount(ccwm1, ccwm2, ccwm3, wins1, wins2, wins3, losses1, losses2, losses3):
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

