FROM python:3.7
RUN pip install pycrypto
COPY modnar.py .
COPY flag.txt .
COPY splash.txt .
EXPOSE 1337
CMD [ "python", "modnar.py" ]
