ImageSearch
===========
Flow and supported features.

1. Click on Search icon to open search box; enter search and click search icon on keyboard

2. images start to load and keyboard is hidden (since it will fill up the screen, so we are loading more than 8 images
(please wait for images to load). If you look at debug logs, it will show you image object ids, but it will take about 15 seconds
or so to load images (as seen on emulator). You can scroll on the screen loading more images. 
Limit: 64 images (as from Google API)

3. Clicking on any image will show it in full screen; there is back arrow at top of this screen to take you back to your search results.
(In case image can't be open, it will show a hazy image meaning image can't be open)

4. Clicking on settings icon on top right will take to filters screen (choice of selecting size, color and type). Only one can be
selected of each of the options (if you uncheck any selected one, it selects "Any" by default); but if you select other option,
previously selected gets clear (would like to incorporate multiple selection in future; except if it is not "Any" selection).
Another option is to put in site (edit text box).
Reset all filters by default sets size / color / type to "any" and leaves site blank.

Note: I am storing these filter options in a file on the device to make it persistent. For the first time, file will be created and since 
it doesn't have anything, no filter selection will be shown; but if you come to app again anytime, it will have your filters selected
from last time (loaded from text file).

5. Click on done when finished and it will show you back search results; please wait again for new images to load based on filters
selected.

6.Does not support sharing or downloading image for now (to be done).