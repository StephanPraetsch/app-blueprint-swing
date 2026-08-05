# App Blueprint Swing

Desktop app blueprint 
* for building cross-platform desktop apps with Swing
* using Java 17 and Maven
* with a SQLite database
* with configuration via menu bar

## Requirements

- maven 3.9+
- java 17+

## Start in development mode

```bash
mvn test
mvn package
java -jar target/app-blueprint-swing-<version>-all.jar
```

## Releasing a New Version

Use only tags in the format `v<semver>`, for example `v0.0.2`.

### Patch release

```bash
./scripts/release.sh patch
```

### Minor release

```bash
./scripts/release.sh minor
```

### Major release

```bash
./scripts/release.sh major
```

Pushing a tag like `v0.0.2` triggers `.github/workflows/build-package.yml` and creates a GitHub release with installers for Linux, macOS, and Windows.

### Linux: `.deb`

```bash
sudo dpkg -i ./*.deb
sudo apt remove appblueprintswing
```

### macOS: `.dmg`

```bash
open ./*.dmg
xattr -dr com.apple.quarantine "/Applications/AppBlueprintSwing.app"
open "/Applications/AppBlueprintSwing.app"
```

The app is not code signed or notarized.

### Windows: `.exe`

execute the `.exe` installer
