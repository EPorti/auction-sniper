Auction Sniper
==============

**Auction Sniper** is an application that watches online auctions and automatically bids slightly higher whenever the price changes, until it reaches a stop-price or the auction closes.

This is a worked example from [GOOS](http://www.growing-object-oriented-software.com/) book.  
Thanks [skinny85](https://github.com/skinny85/goos-book-code) for great step-by-step commit history.

## Domain

- **Item**: something that can be identified and bought.
- **Bidder**: a person or organization that is interested in buying an item.
- **Bid**: a statement that a bidder will pay a given price for an item.
- **Current price**: the current highest bid for the item.
- **Stop price**: the most a bidder is prepared to pay for an item.
- **Auction**: a process for managing bids for an item.
- **Auction house**: an institution that hosts auctions.

## Auction Protocol

The protocol is used for messages between a bidder and an auction house.

Bidder sends _commands_:

- **Join**: a bidder joins an auction.
- **Bid**: a bidder sends a bidding price to the auction.

```
SOLVersion: 1.1; Command: JOIN;
SOLVersion: 1.1; Command: BID; Price: 199;
```

Auction sends _events_:

- **Price**: an auction reports the currently accepted price, minimum increment, and the name of bidder who bid this price.
- **Close**: an auction announces that it has closed. The winner of the last price event has won the auction.

```
SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;
SOLVersion: 1.1; Event: CLOSE;
```

## End-to-End Tests

### Start/stop Openfire server

```sh
$ cd src/e2eTest/fixtures
$ OPENFIRE_VERSION=4.6.0beta docker-compose up
```

```sh
$ OPENFIRE_VERSION=4.6.0beta docker-compose down
```

### Run end-to-end tests

```sh
$ ./gradlew e2eTest
```
