# All_Projects
all the -jar projects have an executable jar file you can double click to lunch, ExcelToWordChart and ExcelToJavaTable both have a few excel table samples in ExcelFilesForTesting.

WaterMeterReader:

"history" = college final project, given time - 6 months give or take.

brief task summery = reading a water meter photo number and sending it to a given phone by SMS.

GUI usages = the user can select from 3 languages English\Hebrew\Russian , add a phone number , select/take a photo to read from , see the algorithm step by step , change algorithm settings , rotate or crop the photo(custom crop function was made since camera crop lose info and in this algorithm its very crucial) , and surely execute the algorithm which sends the result in SMS.

in depth explanation = importing external library called OpenCV(for image processing) and Tesseract OCR(for reading a cleaned image numbers,since library written in c++ NDK usage was required) then applying a grayscale filter on the photo(result in old "black and white" movie before was colored) , applying canny filter with calculated min/max from otsu(result can be seen here: https://en.wikipedia.org/wiki/Canny_edge_detector) , apply HoughLines function which finds lines with length under the given maximum(instead of given maximum created a function that only required how many line samples the user want which can be changed in GUI settings) , applying dilate and erode(can be changed in settings) which used to "fatten" the line abit before counting , using the found lines a custom function chooses the 2 longest lines by counting , using the found lines points we rotate and cut the image , then we draw 2 black lines(size can be adjusted in settings) at the position of found lines to "open" numbers that might be isolated by a square to prepare for Floodfill(https://en.wikipedia.org/wiki/Flood_fill) , using function Floodfill on point(0,0) using color white to fill all the exterior in white except the needed numbers , now we left with the nubmers and noise that needs to be cleaned and it is done using custom function which uses lists and Floodfill to clean the noise(basically seperate each black part horizontically then go on each pixel in that part until reaching a black one then save this point and perform Floodfill+save how many black pixels we lost and repeat till reach end then all left to do is to clean everything except the part that lost most pixels and to clean noise that not in a number part we use certain % of photo hieght(can be changed in settings) , now we left with a clean photo with only black numbers so we apply the OCR function that reads those numbers into a string , and walla the string can be sent to any recipient(added time sent in the SMS as well).

main problems = unknown input which means we dont know the size or location of the numbers , hell we dont even know if the photo contains any numbers , another problem is the size of the photo can easily create out of memory error in android.

solution = the added settings can be adjusted for unique input , for the size problem we made it to preload in a settings size and in case of an error we catch it and load in a lower size until it works.

The Game:

"history" = college java course final project, given time - 1 month.

brief task summery = create a java game which contains a ship and meteors of 2 kinds(one of them is red) , you are given 10 shots and to win the game you need to survive 3 minutes while destroying all the red meteors, failure happens if a red meteor fall or if the ship gets hit by a meteor.

in depth explanation = the meteors and ship are pictures or gif files and by using each picture width and height at certain point its drawen(frame) you can see if they collide so using that you can know if ship got hit or if your shot hit a meteor and have a response accordingly , regarding the speed and angle of the meteors, i used random number generater to set a start point to the meteors, i used random number%screen width-meteor size this way it guarantee that each meteor location will be within screen width and since the red meteors needs to be destroyed i made it bounce if it hits a wall so you will have to destroy them.

