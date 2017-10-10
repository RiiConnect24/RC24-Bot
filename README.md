# RC24 Bot
![License](https://img.shields.io/github/license/riiconnect24/rc24-bot.svg)
![Production List](https://img.shields.io/discord/206934458954153984.svg)
[![CircleCI](https://circleci.com/gh/RiiConnect24/RC24-Bot/tree/java.svg?style=svg)](https://circleci.com/gh/RiiConnect24/RC24-Bot/tree/java)

It took a while, but it's finally here... 🕑
## Introducing a JDA version of RiiConnect24 Bot
or.. rather, what's on master. What is currently running, I can't quite say.

## why exactly did you make it in JDA lol
While `discordrb` is awesome, it usually takes quite a bit for newly added API features to come out. That's fine for most bots, but most in the community wanted the latest as soon as possible.

Also, the fact more users know Java in RiiConnect24's community helped the decision.

## ok ok ok stop wasting my time how do i set it up
I'm glad you asked. A note: We no longer use YAML except for the config (which was just a Ruby class before), as Java really doesn't like YAML.

Check out the following steps:
1. You'll need to have Redis and Gradle installed. (PowerTip ⚡: You can always stick with what version of gradle is being used currently by substituting all usages of `gradle` with `./gradlew` or `./gradlew.bat`.)
2. We'll create the Redis structures as we go, so no need to set anything up. Unless you're converting from the Ruby 💎 version, in which you'll want the *unwritten yaml -> Redis scripts*. They're coming, we promise! 🔜
3. `git clone` the repo somewhere and `cd` into it. 💻
4. It's time to start the 🔨... `gradle build`! And... tada! That's it. 🎉 You can find JAR(s) created in `build/libs`. Easy! 👌👍
5. For running, make sure you have redis installed on whatever machine. Also, if `config.yml` can't be found, it'll copy a default copy to the current directory for you. 🏃

If you don't like emoji in your README, you can blame `Spotlight 🐾#6209` (ID `239809536012058625`). You can also ask them for support. Similarly, if anything goes wrong, feel free to contact `Artuto#0424` (ID `264499432538505217`) on Discord!