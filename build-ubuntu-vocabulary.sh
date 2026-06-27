#!/bin/bash
# Build comprehensive Ubuntu/Linux vocabulary (target: 1GB)

DICT_DIR="dictionaries"
EMBED_DIR="embeddings"

mkdir -p "$DICT_DIR" "$EMBED_DIR"

echo "=== Building Ubuntu/Linux Comprehensive Vocabulary ==="
echo

# 1. System dictionaries
echo "[1/10] Extracting system dictionaries..."
if [ -f /usr/share/dict/words ]; then
    cat /usr/share/dict/words > "$DICT_DIR/english-base.txt"
    echo "  System dict: $(wc -l < $DICT_DIR/english-base.txt) words"
fi

# 2. Man pages vocabulary
echo "[2/10] Extracting from man pages..."
{
    man -k . | awk '{print $1}' | sort -u
    for section in 1 2 3 4 5 6 7 8; do
        apropos -s $section . 2>/dev/null | awk '{print $1}' | tr ',' '\n'
    done
} | sort -u > "$DICT_DIR/manpage-commands.txt"
echo "  Man pages: $(wc -l < $DICT_DIR/manpage-commands.txt) commands"

# 3. Ubuntu packages
echo "[3/10] Extracting Ubuntu package names..."
if command -v apt-cache &> /dev/null; then
    apt-cache pkgnames | sort -u > "$DICT_DIR/ubuntu-packages.txt"
    echo "  Packages: $(wc -l < $DICT_DIR/ubuntu-packages.txt) packages"
fi

# 4. System commands
echo "[4/10] Extracting system commands..."
{
    compgen -c
    ls /usr/bin /bin /usr/sbin /sbin 2>/dev/null
} | sort -u > "$DICT_DIR/system-commands.txt"
echo "  Commands: $(wc -l < $DICT_DIR/system-commands.txt) commands"

# 5. Technical terms
echo "[5/10] Building technical vocabulary..."
cat > "$DICT_DIR/ubuntu-technical.txt" << 'EOF'
ubuntu
linux
kernel
systemd
gnome
kde
xfce
wayland
xorg
apt
dpkg
snap
flatpak
appimage
bash
zsh
terminal
console
shell
desktop
laptop
server
cloud
docker
kubernetes
container
virtual
machine
network
firewall
iptables
ufw
ssh
vpn
wireguard
openvpn
ethernet
wifi
bluetooth
usb
pci
hardware
software
firmware
driver
nvidia
amd
intel
graphics
audio
pulseaudio
pipewire
alsa
display
monitor
resolution
scaling
refresh
hdmi
displayport
partition
filesystem
ext4
btrfs
xfs
ntfs
fat32
mount
unmount
fstab
raid
lvm
encryption
luks
grub
bootloader
uefi
bios
secure-boot
installation
upgrade
update
repository
ppa
source
binary
package
dependency
library
compile
build
make
cmake
gcc
clang
python
java
javascript
nodejs
ruby
go
rust
development
ide
vscode
editor
vim
emacs
nano
git
github
gitlab
version-control
database
mysql
postgresql
sqlite
mongodb
redis
apache
nginx
webserver
proxy
reverse-proxy
load-balancer
ssl
tls
certificate
https
http
dns
dhcp
nat
gateway
router
switch
ip-address
subnet
netmask
cidr
ipv4
ipv6
tcp
udp
icmp
socket
port
protocol
authentication
authorization
password
encryption
hash
checksum
md5
sha256
gpg
pgp
security
vulnerability
patch
exploit
malware
antivirus
backup
restore
snapshot
rsync
tar
gzip
zip
compress
archive
cron
systemctl
service
daemon
process
thread
cpu
ram
memory
swap
cache
buffer
disk
ssd
hdd
nvme
sata
scsi
performance
benchmark
optimization
tuning
configuration
settings
preferences
theme
icon
font
locale
language
timezone
keyboard
mouse
touchpad
trackpad
gamepad
joystick
printer
scanner
webcam
microphone
speaker
headphone
bluetooth-audio
sound-card
video-card
motherboard
bios-settings
overclocking
fan-control
temperature
thermal
cooling
power-management
battery
suspend
hibernate
sleep
shutdown
reboot
restart
login
logout
session
user
group
permission
chmod
chown
sudo
root
superuser
administrator
home-directory
working-directory
path
environment-variable
alias
function
script
automation
cronjob
scheduled-task
log
syslog
journalctl
debugging
troubleshooting
error-message
warning
critical
info
trace
verbose
quiet
output
input
stdout
stderr
stdin
pipe
redirect
stream
buffer-overflow
segmentation-fault
core-dump
stack-trace
EOF
echo "  Technical: $(wc -l < $DICT_DIR/ubuntu-technical.txt) terms"

# 6. Common user queries
echo "[6/10] Building query vocabulary..."
cat > "$DICT_DIR/common-queries.txt" << 'EOF'
how
what
why
when
where
who
install
remove
uninstall
setup
configure
change
modify
update
upgrade
fix
repair
solve
troubleshoot
broken
error
problem
issue
slow
fast
performance
optimize
speed
lag
freeze
crash
hang
stuck
boot
startup
login
password
reset
forgot
recover
restore
backup
save
delete
erase
purge
clean
clear
format
partition
resize
shrink
expand
clone
copy
move
transfer
download
upload
sync
share
network
connect
disconnect
wifi
ethernet
bluetooth
pair
unpair
trust
untrust
display
screen
monitor
resolution
scaling
brightness
contrast
rotation
portrait
landscape
mirror
extend
audio
sound
volume
mute
unmute
speaker
headphone
microphone
input
output
device
hardware
software
driver
firmware
update
kernel
version
check
status
list
show
view
hide
enable
disable
start
stop
restart
reload
refresh
scan
search
find
locate
grep
filter
sort
count
measure
test
benchmark
monitor
watch
track
log
record
capture
screenshot
screencast
video
photo
image
camera
webcam
print
scan
copy
fax
email
browser
firefox
chrome
chromium
edge
safari
opera
brave
vivaldi
internet
web
website
url
link
bookmark
history
cache
cookie
session
download
upload
torrent
vpn
proxy
dns
hosts
firewall
port
protocol
encryption
security
privacy
anonymous
incognito
private
public
shared
local
remote
cloud
server
client
EOF
echo "  Queries: $(wc -l < $DICT_DIR/common-queries.txt) terms"

# 7. Synonyms and variations
echo "[7/10] Building synonym database..."
cat > "$DICT_DIR/synonyms-extended.txt" << 'EOF'
install,setup,add,get,obtain,acquire,download,fetch
remove,uninstall,delete,erase,purge,eliminate
configure,setup,set,adjust,modify,change,customize,tweak
fix,repair,solve,resolve,troubleshoot,remedy,correct,debug
slow,sluggish,laggy,unresponsive,frozen,stuck,hanging
fast,quick,speedy,rapid,swift,responsive
display,screen,monitor,lcd,led,panel
graphics,video,visual,rendering,gpu
audio,sound,music,volume,speaker
network,internet,wifi,ethernet,connection,connectivity
error,problem,issue,bug,fault,glitch,failure
update,upgrade,patch,refresh,sync
browser,web-browser,internet-browser
terminal,console,command-line,shell,cli
desktop,gui,graphical-interface,window-manager
file,document,data,content
folder,directory,path,location
click,select,choose,pick,tap
type,enter,input,write,key
save,store,keep,preserve,retain
load,open,launch,start,run,execute
close,exit,quit,terminate,kill,end
EOF
echo "  Synonyms: $(wc -l < $DICT_DIR/synonyms-extended.txt) entries"

# 8. Typos and misspellings
echo "[8/10] Building typo correction database..."
cat > "$DICT_DIR/typos-comprehensive.txt" << 'EOF'
instal=install
instaling=installing
instalation=installation
configurate=configure
configration=configuration
ubunto=ubuntu
ubnutu=ubuntu
ubunut=ubuntu
linuks=linux
linnux=linux
grafics=graphics
grafx=graphics
graphix=graphics
moniters=monitors
moniter=monitor
screan=screen
scren=screen
srceen=screen
audo=audio
aduio=audio
soud=sound
soung=sound
conect=connect
conecttion=connection
disconect=disconnect
netwrok=network
netowrk=network
newtork=network
comand=command
commnad=command
commadn=command
pacakge=package
packge=package
pakage=package
upate=update
udpate=update
upgarde=upgrade
upgrde=upgrade
removve=remove
remov=remove
delet=delete
broswer=browser
brwoser=browser
firefx=firefox
fiefox=firefox
chromw=chrome
chrom=chrome
termianl=terminal
terinal=terminal
terminl=terminal
kernal=kernel
kermel=kernel
dirver=driver
drivr=driver
nvida=nvidia
nvidea=nvidia
resoltuion=resolution
reoslution=resolution
resoluton=resolution
dispaly=display
dsplay=display
disaply=display
permision=permission
permisions=permissions
directoy=directory
direcotry=directory
commpile=compile
copile=compile
complie=compile
sytem=system
systme=system
systam=system
EOF
echo "  Typos: $(wc -l < $DICT_DIR/typos-comprehensive.txt) corrections"

# 9. Combine all vocabularies
echo "[9/10] Combining vocabularies..."
cat "$DICT_DIR"/*.txt | \
    tr '[:upper:]' '[:lower:]' | \
    tr ',' '\n' | \
    tr '=' '\n' | \
    sort -u | \
    grep -v '^$' > "$DICT_DIR/master-vocabulary.txt"

VOCAB_SIZE=$(wc -l < "$DICT_DIR/master-vocabulary.txt")
echo "  Master vocabulary: $VOCAB_SIZE unique words"

# 10. Estimate size
echo "[10/10] Calculating sizes..."
DICT_SIZE=$(du -sh "$DICT_DIR" | awk '{print $1}')
echo "  Dictionary size: $DICT_SIZE"

echo
echo "=== Vocabulary Build Complete ==="
echo "Total words: $VOCAB_SIZE"
echo "Dictionary location: $DICT_DIR/"
echo
echo "Next steps:"
echo "1. Download GloVe embeddings: ./download-embeddings.sh"
echo "2. Or generate custom embeddings: ./train-embeddings.sh"
