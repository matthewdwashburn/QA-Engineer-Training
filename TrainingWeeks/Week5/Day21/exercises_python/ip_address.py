def ips_between(start, end):
    print(f"Start: {start}")
    print(f"End: {end}")

    split_start_string = start.split(".")
    split_start = [int(x) for x in split_start_string]
    split_end_string = end.split(".")
    split_end = [int(x) for x in split_end_string]

    start_total = (split_start[0] * (256 ** 3)) + (split_start[1] * (256 ** 2)) + (split_start[2] * 256) + split_start[3]
    end_total = (split_end[0] * (256 ** 3)) + (split_end[1] * (256 ** 2)) + (split_end[2] * 256) + split_end[3]

    total_difference = abs(start_total - end_total)

    print(f"Total Diff: {total_difference}")
    return total_difference


def test_ips_between():
    assert ips_between("10.0.0.0", "10.0.0.1") == 1
    assert ips_between("10.0.0.0", "10.0.0.50") == 50
    assert ips_between("10.0.0.0", "10.0.1.0") == 256
    assert ips_between("20.0.0.0", "10.0.0.0") == 167772160
    assert ips_between("0.0.0.0", "255.255.255.255") == 4294967295
    print("All tests passed!")


if __name__ == "__main__":
    test_ips_between()
