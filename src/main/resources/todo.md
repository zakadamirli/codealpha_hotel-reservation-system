# Hostify app

### Needed entities

- Post entity
- activity
- pagination
- customize user activity ex. X hotel all available home
- all owner hosts
- add filter
- list posts all dynamic page most rate limit 10
- payment system integration
- login as user
- login as admin implement in the security part
- favorites
- search
- ~~Review(property)~~, ~~Message (between users)~~, ~~Comment(user)~~


-` Payment, Category, Address, Amenity, Image, Role, Notification, SupportTicket, Wishlist, Rating`

### comment service
  - getAllComments()
  - getOneComment() by CommentId
  - deleteComment() by CommentId
  - getAllComments() by PostId
  - getAllComments() by UserId

### **property service**
- add property
- get property by id
- get all properties
- update property
- delete property 
- get owner(user) properties
- get category(room) properties
- get properties by price range (min,max)  _plan_

````
STANDARD,   // Ən sadə otaq
DELUXE,     // Daha komfortlu, əlavə imkanlarla
SUITE,      // Böyük, bir neçə otaqdan ibarət, ən bahalı
FAMILY,     // Ailə üçün nəzərdə tutulmuş otaq
SINGLE,     // Tək nəfərlik
DOUBLE,     // İki nəfərlik
STUDIO,     // Kiçik mətbəxi olan otaq
PRESIDENTIAL // Lüks prezident otağı
````

```` feature
- "property status change endpoint when create must be true
- then you want change status able to do" 

````
- post service is done
- post controller is done




````
- make and cancel reservation
- payment simulation and booking view
````





```text
@RestController
@RequestMapping("/api/categories")
public class RoomCategoryController {

    @GetMapping
    public RoomCategory[] getAllCategories() {
        return RoomCategory.values();
    }
}
 -frontend de gorunsun deye hansi enumlar var oldugunu
```

