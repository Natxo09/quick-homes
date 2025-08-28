# Quick Homes

A quick and efficient homes management mod for Minecraft servers and singleplayer.

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.8-green)
![Fabric](https://img.shields.io/badge/Fabric-0.17.2-blue)
![Server-Side](https://img.shields.io/badge/Server--Side-Compatible-orange)
![License](https://img.shields.io/badge/License-MIT-purple)

## 🌟 Features

- **Server-Side Compatible** - Works perfectly when installed only on the server
- **Simple Commands** - Easy to use home management system
- **Multi-Dimension Support** - Set homes in any dimension
- **Teleport Animation** - Beautiful animations with configurable delay
- **Smart Language System** - Automatic language detection with server-side fallback
- **Multi-Language Support** - EN, ES, FR, DE, IT, PT-BR
- **Persistent Storage** - Homes are saved per world
- **Highly Configurable** - Customize max homes, teleport delay, language, and more

## 📝 Commands

- `/sethome [name]` - Set a home at your current location
- `/home [name]` - Teleport to a home
- `/delhome [name]` - Delete a home
- `/homes` - List all your homes

## ⚙️ Configuration

The mod creates a configuration file at `config/quickhomes.json` with detailed comments:

```json
{
  "maxHomes": 5,
  "teleportDelay": 3,
  "allowCrossDimension": true,
  "showTeleportAnimation": true,
  
  // Server-Only Mode Settings
  "serverOnlyMode": true,  // Set to true for server-only installations
  "serverLanguage": "en_us" // Language for server messages (en_us, es_es, fr_fr, de_de, it_it, pt_br)
}
```

### Language Configuration

**Quick Homes** features an intelligent language system:

- **Client + Server**: When installed on both sides, clients see messages in their Minecraft language
- **Server Only**: When `serverOnlyMode` is `true`, all players see messages in the `serverLanguage`
- **Single Player**: Always uses your client language

This ensures compatibility whether the mod is installed on the client, server, or both!

## 🚀 Installation

### For Servers
1. Install Fabric server
2. Add Fabric API to the mods folder
3. Add Quick Homes JAR to the mods folder
4. Start the server - clients don't need the mod!

### For Singleplayer
1. Install Fabric loader
2. Add Fabric API to the mods folder
3. Add Quick Homes JAR to the mods folder
4. Launch Minecraft

## 🔧 Building from Source

```bash
git clone https://github.com/Natxo09/quick-homes
cd quickhomes
./gradlew build
```

The compiled JAR will be in `build/libs/`

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 🐛 Issues

Found a bug? Please report it on our [GitHub Issues](https://github.com/Natxo09/quick-homes/issues) page.

