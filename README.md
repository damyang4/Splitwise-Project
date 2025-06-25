# SplitWise Project
A console-based client-server application inspired by Splitwise. Helps groups of friends or roommates easily track and split shared expenses.
# SplitWise 

A console-based client-server application for managing shared expenses between friends, roommates, and groups. Inspired by [Splitwise](https://www.splitwise.com), this app simplifies tracking debts, settling balances, and maintaining transparency in shared spending.

##  Overview

SplitWise allows users to register, form friendships or groups, split bills evenly, track debts, and mark payments as settled. Data is stored persistently on the server in local files, and the system notifies users of updates when they log in.

##  Technologies

- Language: Java
- Communication: Sockets (TCP)
- Data storage: Serialized objects in local files
- Architecture: Multi-threaded client-server
- Optional: Currency conversion using external APIs

---

## Key Features

- User registration and login
- Add friends and create groups
- Split expenses between friends or entire groups
- Track debts and receivables in real-time
- Notifications on login about payments or new expenses
- Payment approval (only the person who is owed can confirm)
- View full status or only unsettled balances
- View personal transaction history
- Persistent storage across sessions (user and transaction files) 
- Error logging with user-friendly messages
- (Bonus) Currency conversion using public exchange rate API

---

## Sample Commands

```bash
$ register alex mypassword
$ login alex mypassword

$ add-friend pavel97
$ create-group trip-to-greece pavel97 ico_h maria94

$ split 20 pavel97 groceries
$ split-group 60 trip-to-greece beach bar

$ get-status
$ payed 10 pavel97

$ history
$ switch-currency EUR

