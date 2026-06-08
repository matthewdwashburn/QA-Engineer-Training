file=open("numbers.dat","wb")
file.write(bytes([10,20,20,40]))
file.close()

file=open("numbers.dat","rb")
data=file.read()
print(data)
file.close()