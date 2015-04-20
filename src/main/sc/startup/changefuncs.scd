~java.addDefinition("PercentWalk",
	\changeFunc,
	{
		| value, min, max, walkAmount |

		var changeTop, change;
		changeTop = (max - min) * (walkAmount / 100);
		change = changeTop.rand * [1, -1].choose;
		value + change;
	},
	[[\walkAmount, 0, 100, 10]]

);
// Chance to go up or down

~java.addDefinition("TrueRandom",
	\changeFunc,
	{
		| value, min, max |
		rrand(min, max);
	}
);

~java.addDefinition("SteadyAction",
	\taskRunner,
	{
		| object, timeBetween |
		loop { // Go forever
			object.sendActionMessage;
			timeBetween.value.wait;
		}
	},
	[[\timeBetween, 0, 10, 1]]
);

~java.addDefinition("SimpleAction",
	\taskRunner,
	{
		| object |
		object.sendActionMessage;

	}
);

~java.addDefinition("RandomAction",
	\taskRunner,
	{
		| object, minSeconds, maxSeconds |
		loop { // Go forever
			var secondsToWait = rrand(minSeconds.value, maxSeconds.value);
			"Performing action message".postln;
			object.sendActionMessage; // Could concievably split up into multiple actions
			secondsToWait.wait;
		}
	},
	[[\minSeconds, 0, 10, 1],
		[\maxSeconds, 0, 10, 2]]
);


~java.addDefinition("RoutinePlayer",
	\routinePlayer,
	{
		| player |
		Prout({| ev |
			var pat;
			block { |break|
				loop {
					pat = Pbindf(*[
						player.template, // Connected instrument data
						#[\midinote, \dur], Pseq(player.pattern.value) // the current pattern's value
					]);
					"Doing a pattern".postln;
					player.doPlayedAction; // Run the action to do when played (probably OSC message)
					ev = pat.embedInStream(ev); // Embed the pattern and wait for it to be played
				}
			}
		});
	}
);







