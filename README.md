# RC24 Bot
![MIT Licensed](https://img.shields.io/github/license/riiconnect24/file-maker.svg)

This bot was thought of by PokeAcer, created by Seriel, and is currently being improved on by Seriel, foxlight_is_ok and Larsenv for the RiiConnect24 Discord server. Support for other servers is in progress and can be unstable.

## Self-Hosting:
### Requirements
- Ruby
- bundler (`gem install bundler`)

### Install
1. Clone the repo: `git clone https://github.com/RiiConnect24/RC24-Bot.git`
2. cd into the repo: `cd RC24-Bot`
3. Edit `config.rb.example` to include the needed information and save it as `config.rb`
4. Install bundler if you haven't already: `gem install bundler`
5. Install the bundle: `bundle install`
6. ~~Create 3 roles on your server: Moderators, Bot Helpers, and Developers.
<br/>(Bot Helpers are above Moderators in the way RC24 Bot handles things.)~~
<br/> You can still do the above step, however RC24-Bot now has a config command so you can
adjust role names if required. The above role names will be looked for as a default.
7. (optional) Edit `data/settings.yml` to include custom errors.
All of these codes will be run with FORE/NEWS.
```yaml

---
local_codes:
  news:
    1: Can't open the VFF
    2: WiiConnect24 file problem
    3: VFF file corrupted
    4: Unknown (it probably doesn't exist)
    5: VFF processing error
    6: Invalid data
    '000099': Other error
  notes:
    102032: The IOS your app uses is not patched for RiiConnect24, and you took too
      long.
```

8. Run the bot. For Linux: `./run_linux.sh`. For Windows: `run_windows.bat`.

### Updating
1. Pull any changes from the repo: `git pull`
2. Check any config changes. Open `config.rb.example` and see if any new information is
needed in your `config.rb`.
3. Update the bundle: `bundle update`
4. Run the bot: `./run_linux.sh` or `run_windows.bat`

Please report any issues to `@Seriel#0757` | `<@228574821590499329>` 
or `@foxlight_is_ok#6129` | `<@239809536012058625>` on Discord, or open an issue on GitHub!

Enjoy~
<br/>

# Credits

Kudos to the following users for helping Seriel out:

- luigoalma (https://github.com/luigoalma)
- megumi (https://github.com/megumisonoda)
- Anyone active on the Discordrb channel in the Discord API server.
- meew0 for Discordrb (https://github.com/meew0/discordrb)
