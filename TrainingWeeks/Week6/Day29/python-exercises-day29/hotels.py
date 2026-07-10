class hotels:
    def allocate_rooms(customers):
        room_dict = {}
        n_customers = len(customers)
        assignments = [0] * n_customers
        order = sorted(range(n_customers), key= lambda w: customers[w][0]) # Order by earliest arrival time, take notes
        for i in order:
            customer = customers[i]
            arrival_day = customer[0]
            departure_day = customer[1]
            found_room = False
            current_room = 1
            while not found_room:
                # Room has not been assigned
                if current_room not in room_dict:
                    # Assign customer to this room
                    room_dict[current_room] = []
                    room_dict[current_room].append(customer)
                    found_room = True
                    assignments[i] = current_room
                else: # Room has been assigned
                    open = True
                    # Check if room has customer's requested booking dates
                    for assignment in room_dict[current_room]:
                        room_book_start = assignment[0]
                        room_book_end = assignment[1]
                        # Booked, skip the rest of assignments
                        if ((arrival_day >= room_book_start and arrival_day <= room_book_end) 
                            or (departure_day >= room_book_start and departure_day <= room_book_end)
                            or (arrival_day < room_book_start and departure_day > room_book_end)
                            ):
                            open = False
                            break
                    # Room has openings during customer's dates
                    if open:
                        assignments[i] = current_room
                        room_dict[current_room].append(customer)
                        found_room = True
                    # Room is booked during customer's requested booking dates
                    current_room += 1     
        return assignments