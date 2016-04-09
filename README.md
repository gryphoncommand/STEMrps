# STEMrps
Rock paper scissors game! The machine learns how you play, and thinks of a strategy to beat you, based on your last moves.

## Usage

### Setup
Included is a zip archive (located under \server\) to get you started if you want to set up your own twitter bot.


Simply include a jar from the current built repo, and supply an oath.txt (mine only has placeholders) with the appropriate API keys, and start by running setup.bat . This creates a folder (for windows users) in C:\STEMrps\ where we store all of our data.


From there, run the jar itself with no arguments to play offline (cmd-line rps game), or run start.bat to use whatever twitter account you have supplied with oath.txt.

## Twitter
Currently running on my twitter bot account (@CloudComputeBot). Some basic tweet formats are shown:

### Playing the game
To play rock paper scissors with the bot, tweet it as such:


`@CloudComputeBot <r|p|s|rock|paper|scissors> [additional args]`
`<r|p|s|rock|paper|scissors>` is what you want to play. Can be either the initial or the full word. The bot is very lenient on how you write things, for example, if some words fail, it continues reading until it finds a suitable play, so for example tweeting
`@CloudComputeBot I am going to play paper` would continue to scan until it got to player, and it would, correctly, record your move as paper.


With this feature, you can add superflous text, and as long as you have one of the six aliases, it will detect your input correct


If it doesn't find anything suitable, it returns your timestamp of your error

### Getting statistics
You can also retrieve a user's statistics (or your own) through the following syntax:


`@CloudComputeBot stats <me|username|blank> [extra args]`


If left blank after stats, it shows you your own stats, so try tweeting:


`@CloudComputeBot stats`


And it returns percent win, percent tie, percent loss, usage of different moves, etc.
If supplied with a user's twitter name (without the @ sign), it retrieves that user's history, so to get my play history:


`@CloudComputeBot stats Sm0oth_kriminal`


This will work as long as the first three Strings you pass in are "@CloudComputeBot", "stats", and a username. You can enter in other things afterwards.
