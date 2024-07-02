import os
import subprocess
import time
import signal
from datetime import datetime

def make_executable():
    try:
        subprocess.run('chmod +x *', shell=True, check=True)
        print("All files in the current directory are now executable.")
    except subprocess.CalledProcessError as e:
        print(f"Error making files executable: {e}")

def log(message):
    current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{current_time}] {message}")

def run_bot():
    while True:
        try:
            process = subprocess.Popen(['python3', '/workspaces/opth/m.py'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            log("Bot started.")
            stdout, stderr = process.communicate()

            if process.returncode != 0:
                log(f"Bot crashed with error: {stderr.decode()}")
            else:
                log("Bot exited normally.")
                
        except Exception as e:
            log(f"An error occurred while running the bot: {e}")

        log("Restarting bot in 5 seconds...")
        time.sleep(5)

def signal_handler(sig, frame):
    log(f"Signal {sig} detected. Restarting bot...")
    run_bot()

if __name__ == "__main__":
    make_executable()
    log("Bot script starting...")
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)  # Handle termination signals
    run_bot()


