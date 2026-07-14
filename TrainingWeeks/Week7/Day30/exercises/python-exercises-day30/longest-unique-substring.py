def longest_substring(s : str) -> int:
    longest_length = 0
    seen_list = []
    for i in range(len(s)):
        seen_list.clear()
        local_length = 0
        for j in range(i, len(s)):
            if s[j] not in seen_list:
                local_length += 1
                seen_list.append(s[j])
            else:
                break
        if local_length > longest_length:
            longest_length = local_length
    return longest_length