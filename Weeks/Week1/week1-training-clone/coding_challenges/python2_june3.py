#https://www.codewars.com/kata/696503a831e8e76d36706fb9/train/python

class Snake:
    def __init__(self, length):
        self.length = length

    def __sub__(self, other):
        return Snake(self.length + 2)

    def __len__(self):
        return self.length


class SnakeEye:
    def __gt__(self, other):
        return Snake(5)
    
o=SnakeEye()
print(len((o > o) - 0 - 0 - 0 - 0))