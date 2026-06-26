from collections import Counter


def shoe_store():
    input() # shoe count, not used
    stock = Counter(input().split()) # key is size, value is count
    customer_count = int(input())

    sales = 0

    for _ in range(customer_count):
        size, offer = input().split()
        if int(stock[size]) > 0:
            stock[size] -= 1
            sales += int(offer)

    return sales


def main():
    print(shoe_store())


if __name__ == "__main__":
    main()
