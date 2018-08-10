# RC24 Bot
![License](https://img.shields.io/github/license/riiconnect24/rc24-bot.svg)
![Production List](https://img.shields.io/discord/206934458954153984.svg)
[![CircleCI](https://circleci.com/gh/RiiConnect24-Bot/RC24-Bot/tree/java.svg?style=svg)](https://circleci.com/gh/RiiConnect24/RC24-Bot/tree/java)

It took a while, but it's finally here...

## Introducing a JDA version of RiiConnect24 Bot
or.. rather, what's on master. What is currently running, I can't quite say.

## Why does this bot use JDA
While discordrb is awesome, it usually takes quite a bit for newly added API features to come out. That's fine for most bots, but most in the community wanted the latest as soon as possible.

Also, the fact more users know Java in RiiConnect24's community helped the decision.

## How do i set up the bot to host it myself?
I'm glad you asked. 
##### A note: We no longer use [YAML](http://yaml.org/) except for the config (which was just a [Ruby](https://www.ruby-lang.org/) class before), as [Java](https://www.java.com/) really doesn't like [YAML](http://yaml.org/).

Check out the following steps:
1. You'll need to have Redis and Maven installed.
2. We'll create the Redis structures as we go, so no need to set anything up.
3. `git clone` the repo somewhere and `cd` into it.
4. It's time to start building... Run `mvn install`! And... tada! That's it. You can find JAR(s) created in `target`. Easy!
5. For running, make sure you have redis installed on whatever machine. Also, if `config.yml` can't be found, it'll copy a default copy to the current directory for you.

If you need support for the bot, head on over to [the bot's official support server](https://discord.gg/PVsh4jP) or DM `Artuto#0424 | 264499432538505217` in Discord.

For more infos about JDA, [click here](https://github.com/DV8FromTheWorld/JDA).
