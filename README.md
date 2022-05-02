# Square2Signage
A Java based micro service using Spring Boot framework to facilitate curating the display of a Square Seller's inventory
and providing it in a format consumable by digital signage.

Additional documents such as Postman collection and design documents can be found in [docs folder](/docs).

## Local Run
To configure the Square2Signage application to run locally you'll need to follow all the steps below for both
[Square App Information](#Square-App-Information) and [Java Configuration](#Java-Configuration).

### Square App Information
1. Go to [Square Developer Dashboard](https://developer.squareup.com/apps) and login
2. Select the app and then select **Sandbox**
3. Copy the **Sandbox Application ID** and **Sandbox Access token** for later
4. On the left navigation panel click on **OAuth**
5. Set the **Sandbox Redirect URL** to `http://localhost:8080/square/oauth/finish`
6. Navigate back to [Square Developer Dashboard](https://developer.squareup.com/apps) and create a test account if you haven't done so
7. Open the test account you'll be using for testing

### Java Configuration
1. Set your runtime environment variables as the following, replacing `[variable-name]` with the proper variable
   1. `SQUARE_APP_ID=[Sandbox Application ID]`
   2. `SQUARE_APP_SECRET=[Sandbox Access token]`
   3. (Optional, default is `localhost:3000`) `FRONTEND_HOST=[Host for frontend]`
   4. (Optional, default is `http`) `FRONTEND_HTTP_SCHEME=[http or https]`
   5. (Optional, default is `/failure`) `FRONTEND_FAILURE_PATH=[Path to frontend failure page]`
   6. (Optional, default is `/success`) `FRONTEND_SUCCESS_PATH=[Path to frontend success page]`
2. Enable annotation processing in your IDE ([Guide](https://www.baeldung.com/lombok-ide)) as this project uses Lombok

Now you should be able to run the application locally and either call its APIs via Postman (after OAuth flow) or via your
frontend web app.

## OAuth Flow via Browser
You can manually start the Sqaure OAuth flow by going here http://localhost:8080/square/oauth/start