### Voting system for deciding where to have lunch.
* 2 types of users: administrator and regular users
* Users can register, vote for the restaurant, manage their profile and view their voting history
* Admin can create / edit / delete users.
* Admin maintains a list of dishes for the menu
* Admin can input a restaurant and it's lunch menu of the day
* Menu changes each day (admins do the updates)
* Users can vote on which restaurant they want to have lunch at
* Only one vote counted per user
* If user votes again the same day:
  - If it is before 11:00 we assume that he changed his mind.
  - If it is after 11:00 then it is too late, vote can't be changed
* For each restaurant in the daily menu, the dish can be specified only once. The dish cannot be deleted if used in the menu.
* Voting history and menus are stored, filtering by date is possible.

_Work with the system is implemented via UI (via AJAX) and via REST interface with basic authorization.
The time until which voting is possible is set in the "system.properties" settings file. The date and time of the vote are passed from the client's browser, considering the time zone.
Spring caching is used - caching a request for a list of restaurants, menu of the current day when voting.
The "Admin" user cannot be deleted. When registering / editing a user, the uniqueness of the name (case-insensitive) and email is checked.
Implemented localization of the user interface with a choice of language from the list on the main page.
The REST portion of the interface is covered with JUnit tests using Spring MVC Test and Spring Security Test._

### __['Voting system' on HEROKU](http://restvoting.herokuapp.com/)__


### __curl samples__ (application is deployed in context 'restvoting'):

### _Users_:

#### get All Users
`curl -s http://localhost:8080/restvoting/rest/admin/users -u Admin:1111`

#### get User 1000
`curl -s http://localhost:8080/restvoting/rest/admin/users/1001 -u Admin:1111`

#### get User not found
`curl -s -v http://localhost:8080/restvoting/rest/admin/users/2000 -u Admin:1111`

#### get Voting history
`curl -s "http://localhost:8080/restvoting/rest/profile/voting_history?start_date=2021-05-01&end_date=2021-05-02" -u User1:2222`

#### register User
`curl -s -i -X POST -d '{"name":"NewUser","email":"new_user@mail.ru","password":"abcd"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/profile/register`

#### create User
`curl -s -i -X POST -d '{"name":"NewUser2","email":"new_user2@mail.ru","password":"abcd"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/admin/users -u Admin:1111`

#### update User profile
`curl -s -i -X PUT -d '{"name":"UpdatedUser","email":"new_mail@mail.ru","password":"psw1"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/profile -u User1:2222`

#### update User 1002
`curl -s -i -X PUT -d '{"name":"UpdatedUser2","email":"new_mail2@mail.ru","password":"psw2"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/admin/users/1002 -u Admin:1111`

#### update User - with validate error
`curl -s -i -X PUT -d '{"name":"UpdatedUser2","email":"new_mail2@mail.ru","password":"123"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/admin/users/1002 -u Admin:1111`

#### delete User - with error 403 Forbidden
`curl -s -X DELETE http://localhost:8080/restvoting/rest/admin/users/1002 -u User3:abcd`

#### delete User (profile)
`curl -s -X DELETE http://localhost:8080/restvoting/rest/profile -u UpdatedUser:psw1`

#### delete User 1002
`curl -s -X DELETE http://localhost:8080/restvoting/rest/admin/users/1002 -u Admin:1111`



### _Voting_:

#### Cast vote (first time or before 11:00)
`curl -s -X POST http://localhost:8080/restvoting/rest/voting/1000?voteDateTime=2021-12-11T12:10:00 -u User3:abcd`

#### Cast vote (again after 11:00) - with validation error
`curl -s -X POST http://localhost:8080/restvoting/rest/voting/1000?voteDateTime=2021-12-11T12:20:00 -u User3:abcd`

#### Cast vote (invalid voting date) - with validation error
`curl -s -X POST http://localhost:8080/restvoting/rest/voting/1000?voteDateTime=2021-12-10T10:30:00 -u User3:abcd`



### _Menu_:

#### Get menu for voting (cacheable)
`curl -s "http://localhost:8080/restvoting/rest/menu/voting/1000?date=2021-05-01" -u User3:abcd`

#### Get menu history
`curl -s "http://localhost:8080/restvoting/rest/menu/history/1000?date=2021-05-01" -u User3:abcd`



### _Dishes_:

#### get All Dishes
`curl -s http://localhost:8080/restvoting/rest/admin/dishes -u Admin:1111`

#### get Dish 1000
`curl -s http://localhost:8080/restvoting/rest/admin/dishes/1000 -u Admin:1111`

#### get Dish not found
`curl -s http://localhost:8080/restvoting/rest/admin/dishes/10000 -u Admin:1111`

#### update Dish 1000
`curl -s -i -X PUT -d '{"name":"Updated dish","weight":1234}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/admin/dishes/1000 -u Admin:1111`

#### update Dish - with validate error
`curl -s -i -X PUT -d '{"name":"U","weight":12345}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/admin/dishes/1000 -u Admin:1111`

#### create Dish
`curl -s -i -X POST -d '{"name":"Created dish","weight":999}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/admin/dishes -u Admin:1111`

#### create Dish - with validate error
`curl -s -i -X POST -d '{"name":"Created dish","weight":1}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/restvoting/rest/admin/dishes -u Admin:1111`

#### delete Dish 1006
`curl -s -X DELETE http://localhost:8080/restvoting/rest/admin/dishes/1006 -u Admin:1111`

#### delete Dish - with data integrity error
`curl -s -X DELETE http://localhost:8080/restvoting/rest/admin/dishes/1002 -u Admin:1111`