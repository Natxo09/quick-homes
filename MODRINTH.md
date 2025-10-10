# Quick Homes

**The fastest and most efficient homes management mod for Minecraft servers!**

## ✨ Why Quick Homes?

Quick Homes is a lightweight, server-side teleportation mod that brings the essential home management features you need without any client-side requirements. Perfect for both small survival servers and large multiplayer communities.

## 🎯 Key Features

### 🖥️ **Smart Server-Side Compatibility**
Works perfectly as a server-only mod! Players can join with vanilla Minecraft, and the mod intelligently adapts to show messages in the server's configured language. When clients have the mod, they see translations in their preferred language.

### 🏠 **Simple Home Management**
- Set multiple homes with custom names
- Teleport instantly to your saved locations
- Manage homes across all dimensions
- Clean and intuitive command system

### ⚡ **Performance Focused**
- Lightweight and optimized code
- Minimal server impact
- Efficient data storage
- Auto-save system to prevent data loss

### 🎨 **Customizable Experience**
- Configure maximum homes per player
- Adjustable teleport delay with countdown
- Toggle cross-dimension teleportation
- Beautiful teleport animations

### 🌍 **Intelligent Multi-Language System**

**Smart Language Detection:**
- **Both Sides:** Clients see messages in their Minecraft language
- **Server Only:** Messages display in the server's configured language
- **Single Player:** Always uses your preferred language

**Supported Languages:**
- English (en_us)
- Español (es_es)
- Deutsch (de_de)
- Français (fr_fr)
- Italiano (it_it)
- Português Brasileiro (pt_br)
- 简体中文 (zh_cn)

## 📝 Commands

All commands are simple and intuitive:

- `/sethome [name]` - Save your current location as a home
- `/home [name]` - Teleport to a saved home
- `/delhome [name]` - Remove a saved home
- `/homes` - View all your saved homes

If no name is provided, commands default to "home".

## ⚙️ Configuration

Customize the mod to fit your server's needs:

```json
{
  "maxHomes": 5,                  // Maximum homes per player
  "teleportDelay": 3,             // Seconds before teleportation
  "allowCrossDimension": true,    // Allow teleportation between dimensions
  "showTeleportAnimation": true,  // Show particles during teleport
  
  // Server-Only Mode Settings
  "serverOnlyMode": true,         // Enable for server-only installations
  "serverLanguage": "en_us"       // Server language (en_us, es_es, fr_fr, de_de, it_it, pt_br)
}
```

**Configuration Notes:**
- `serverOnlyMode`: Set to `true` when only installed on the server
- `serverLanguage`: Choose the language for all server messages
- The config file includes helpful comments for easy setup

## 🚀 Installation

### For Server Administrators:
1. Install Fabric Server (1.21.8)
2. Add Fabric API to your mods folder
3. Drop Quick Homes into your mods folder
4. Start your server - that's it!

### For Singleplayer:
1. Install Fabric Loader (1.21.8)
2. Add Fabric API to your mods folder
3. Add Quick Homes to your mods folder
4. Launch Minecraft and enjoy!

## 💡 Perfect For:

- **Survival Servers** - Give players quality of life improvements
- **Creative Servers** - Quick navigation between builds
- **Hub Servers** - Personal waypoint system
- **Private SMPs** - Essential teleportation without complexity
- **Public Servers** - Safe, controlled teleportation system
- **International Servers** - Automatic language support for global communities

## 🔒 Safety Features

- **Movement Detection** - Teleportation cancels if you move
- **Safe Landing** - Automatically finds safe spots to teleport
- **Dimension Validation** - Ensures safe cross-dimension travel
- **Permission Compatible** - Works with permission plugins (LuckPerms, etc.)
- **Smart Retry System** - Automatic teleport retry on minor movements

## 📊 Technical Details

- **Minecraft Version:** 1.21.8
- **Mod Loader:** Fabric
- **Fabric API:** Required
- **Environment:** Server & Client (Server-side only for multiplayer)
- **License:** MIT

## 🤝 Open Source

Quick Homes is open source! Check out the code, report issues, or contribute:
- [GitHub Repository](https://github.com/Natxo09/quick-homes)
- [Issue Tracker](https://github.com/Natxo09/quick-homes/issues)

---

*Quick Homes - Making Minecraft travel quick and easy!*