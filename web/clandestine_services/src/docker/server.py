#!/usr/bin/env python

import socket
import thread

flag = "ATR[ch33s3cak3]"
responses = ["Nope", "Uhnuh", "Nada", "incorrect", "That's not the way to greet someone."]


def on_new_client(conn, addr, buffer_size):
    count = 0
    while(True):
      try:
        data = conn.recv(buffer_size)
      except Exception as e:
        print("ERROR #1: {0}".format(e))
        break
      if data == '\r\n' or data == '\n': 
        break
      try:
        data = data.replace('\r','')
        data = data.replace('\n','')
      except Exception as e:
        print("ERROR #2: {0}".format(e))
        break
      try:
        print("received data: {0}".format(repr(data)))
      except Exception as e:
        print("ERROR #3: {0}".format(e))
        break
      try:
        if data == "hello" or data == "HELLO" or data == "Hello":
          print("ACCEPT")
          conn.send("Hello there!\n{0}\n".format(flag))
        else:
          conn.send("{0}\n".format(responses[count%len(responses)]))
          count = count + 1
      except Exception as e:
        print("ERROR #4: {0}".format(e))
        break
    conn.close()

def main():
  TCP_IP = '127.0.0.1'
  TCP_PORT = 1337
  BUFFER_SIZE = 1024

  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  s.bind((TCP_IP, TCP_PORT))
  s.listen(5)

  while True:
    c, addr = s.accept()
    print('Connection address: {0}'.format(addr))
    thread.start_new_thread(on_new_client,(c,addr, BUFFER_SIZE))
  s.close()

# def main_old():
#   count = 0
#   TCP_IP = ''
#   TCP_PORT = 1337
#   BUFFER_SIZE = 1024

#   s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#   s.bind((TCP_IP, TCP_PORT))
#   s.listen(1)

#   conn, addr = s.accept()
#   print('Connection address: {0}'.format(addr))


#   ##TODO

#   while(True):
#     try:
#       data = conn.recv(BUFFER_SIZE)
#     except Exception as e:
#       print("ERROR #1: {0}".format(e))
#       break
#     if data == '\r\n' or data == '\n': 
#       break
#     try:
#       data = data.replace('\r','')
#       data = data.replace('\n','')
#     except Exception as e:
#       print("ERROR #2: {0}".format(e))
#       break
#     try:
#       print("received data: {0}".format(repr(data)))
#     except Exception as e:
#       print("ERROR #3: {0}".format(e))
#       break
#     try:
#       if data == "hello" or data == "HELLO":
#         print("ACCEPT")
#         conn.send("Why Hello there\n{0}\n".format(flag))
#       else:
#         conn.send("{0}\n".format(responses[count%len(responses)]))
#         count = count + 1
#     except Exception as e:
#       print("ERROR #4: {0}".format(e))
#       break
#   conn.close()

# def main_new():
#   s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#   count = 0

#   TCP_IP = '127.0.0.1'
#   TCP_PORT = 1337
#   BUFFER_SIZE = 1024

#   s.bind((TCP_IP, TCP_PORT))

#   while True:
#           s.listen(0)
#           client, address = s.accept()
#           print "{} connected".format( address )

#           try:
#             data = client.recv(BUFFER_SIZE)
#           except Exception as e:
#             client.close()
#             print("ERROR #1: {0}".format(e))
#             continue

#           try:
#             data = data.replace('\r','')
#             data = data.replace('\n','')
#           except Exception as e:
#             client.close()
#             print("ERROR #2: {0}".format(e))
#             continue

#           try:
#             print("received data: {0}".format(repr(data)))
#           except Exception as e:
#             client.close()
#             print("ERROR #3: {0}".format(e))
#             continue

#           try:
#             if data == "hello" or data == "HELLO":
#               client.send("Hello there!\n{0}\n".format(flag))
#               client.close()
#             else:
#               client.send("{0}\n".format(responses[count%len(responses)]))
#               count = (count + 1)%len(responses)
#               continue
#           except Exception as e:
#             client.close()
#             print("ERROR #4: {0}".format(e))
#             continue
#   print("Close")
#   client.close()
#   s.close()

if __name__ == '__main__':
  main()