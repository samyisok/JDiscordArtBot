# JDiscordArtBot

JDiscordArtBot is a Discord bot made for fun to use in art-themed servers.

## Features

- **Artstation Integration:**  
  `%art` — Get the top random image from Artstation.
- **Q&A:**  
  `%!`, `%эта`, `%эти`, `%это` — Get answers to questions.
- **Help:**  
  `%help`, `%хелп`, `%херп` — Get a help image.  
  `%addhelp` — Upload a help image.
- **Ping:**  
  `%ping`, `%пинг` — Get a pong response from the bot.
- **Anime & Waifu Generation:**  
  `%anime` — Get a random anime image, if you don't know what to watch.  
  `%waifu` — Generate waifu stats for drawing inspiration.

## Configuration

Main config is in `core/src/main/resources/application.properties`. Key properties:
- `jda.botkey` — Discord bot token (set via env variable)
- `command.prefix` — Command prefix (default: `%`)
- `file.save.path` — Path for saved files
- `spring.datasource.*` — Database connection settings (default: H2)
- `limiter.tries.max` and `limiter.tries.ms` — Rate limiter settings

## Getting Started

### Build & Launch

1. **Set Environment Variables:**
   - `JDA_API_KEY` (Discord bot token)
   - Optional: `DB_URL`, `DB_USERNAME`, etc.

2. **Build and Run:**
   ```powershell
   .\mvnw.cmd clean package
   run.bat
   ```

   Example `run.bat`:
   ```bat
   @echo off
   setlocal
   set DB_URL=jdbc:h2:file:d:/props/jdab/data/mydb
   set FILE_SAVE_PATH=d:/props/jdab/download
   set LOG_FILE_PATH=d:/props/jdab/
   set JDA_API_KEY=<DISCORDKEY>
   set COMMAND_PREFIX=%
   call ./mvnw spring-boot:run -f ./core
   endlocal
   ```

3. **Deploy:**
   ```powershell
   .\mvnw.cmd clean package
   java -jar core\target\sarah-0.0.1-SNAPSHOT.jar
   ```

4. **Systemd Service:**
   To run as a Linux service, use the following example config:
   ```ini
   [Unit]
   Description=JDiscordArtBot Discord Bot Service
   After=network.target

   [Service]
   Type=simple
   User=discordbot
   WorkingDirectory=/home/discordbot/JDiscordArtBot
   ExecStart=/usr/bin/java -jar core/target/sarah-0.0.1-SNAPSHOT.jar
   Environment=JDA_API_KEY=<DISCORDKEY>
   Environment=DB_URL=jdbc:h2:file:/home/discordbot/JDiscordArtBot/data/mydb
   Environment=FILE_SAVE_PATH=/home/discordbot/JDiscordArtBot/download
   Environment=LOG_FILE_PATH=/home/discordbot/JDiscordArtBot/logs
   Environment=COMMAND_PREFIX=%
   Restart=on-failure
   RestartSec=10

   [Install]
   WantedBy=multi-user.target
   ```
   **Setup:**
   1. Copy the file to `/etc/systemd/system/jdiscordartbot.service`
   2. Reload systemd: `sudo systemctl daemon-reload`
   3. Enable: `sudo systemctl enable jdiscordartbot`
   4. Start: `sudo systemctl start jdiscordartbot`
   5. Check status: `sudo systemctl status jdiscordartbot`

5. **Kubernetes:**
   - See `k8s/` for manifests and deployment scripts.

## Test Coverage (JaCoCo)

To generate and view JaCoCo test coverage reports:

1. **Run tests with coverage:**
   ```powershell
   .\mvnw.cmd clean test
   ```

2. **Generate JaCoCo report:**
   ```powershell
   .\mvnw.cmd jacoco:report
   .\mvnw.cmd jacoco:report-aggregate
   ```

3. **View the report:**
   - Open `aggregate\target\site\jacoco-aggregate\index.html`.

## License

This project is licensed under the MIT License.


