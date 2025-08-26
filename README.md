# Quick Homes

A quick and efficient homes management mod for Minecraft servers and singleplayer.

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.8-green)
![Fabric](https://img.shields.io/badge/Fabric-0.17.2-blue)
![Server-Side](https://img.shields.io/badge/Server--Side-Compatible-orange)
![License](https://img.shields.io/badge/License-MIT-purple)

## 🌟 Features

- **Server-Side Only** - Clients don't need to install the mod to join your server
- **Simple Commands** - Easy to use home management system
- **Multi-Dimension Support** - Set homes in any dimension
- **Teleport Animation** - Beautiful animations with configurable delay
- **Multi-Language** - Supports EN, ES, FR, DE, IT, PT-BR
- **Persistent Storage** - Homes are saved per world
- **Highly Configurable** - Customize max homes, teleport delay, and more

## 📝 Commands

- `/sethome [name]` - Set a home at your current location
- `/home [name]` - Teleport to a home
- `/delhome [name]` - Delete a home
- `/homes` - List all your homes

## ⚙️ Configuration

The mod creates a configuration file at `config/quickhomes.json`:

```json
{
  "maxHomes": 5,
  "teleportDelay": 3,
  "allowCrossDimension": true,
  "showTeleportAnimation": true
}
```

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

