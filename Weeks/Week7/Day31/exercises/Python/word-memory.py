def mutations(alice, bob, word, first):
    current_word = word
    winning = -1
    turn = first
    alice = list(alice)
    bob = list(bob)

    # Remove starting word from both alice and bob's vocab
    if word in alice:
        alice.remove(word)
    if word in bob:
        bob.remove(word)

    # Find word method
    def findWordInMemory(winning, turn, current_word, current_player_set, opposing_player_set):
        for temp_word in current_player_set:
            differences = sum(1 for a,b in zip(temp_word, current_word) if a != b)
            if differences == 1 and len(temp_word) == len(set(temp_word)):
                current_word = temp_word
                current_player_set.remove(temp_word)
                if temp_word in opposing_player_set:
                    opposing_player_set.remove(temp_word)
                turn = 1 if turn == 0 else 0
                winning = 0 if turn == 1 else 1
                return winning, turn, current_word, True
        turn = 1 if turn == 0 else 0
        return winning, turn, current_word, False
    
    first_turn = True
    while True:
        # Alice's turn
        if(turn == 0):
            winning, turn, current_word, found = findWordInMemory(winning, turn, current_word, alice, bob)
            if not found:
                if first_turn:
                    first_turn = False
                    continue
                else:
                    break
            first_turn = False
            continue
        # Bob's turn
        if(turn == 1):
            winning, turn, current_word, found = findWordInMemory(winning, turn, current_word, bob, alice)
            if not found:
                if first_turn:
                    first_turn = False
                    continue
                else:
                    break
            first_turn = False
            continue
    return winning
