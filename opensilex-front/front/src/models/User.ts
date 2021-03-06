import * as jwtDecode from "jwt-decode";
import { VueCookies } from 'vue-cookies'
import VueI18n from 'vue-i18n'

declare var $cookies: VueCookies;

export class User {
    private static INACTIVITY_PERIOD_MIN = 10;
    private firstName: string = "Jean";
    private lastName: string = "Dupont";
    private email: string = "jean.dupont@opensilex.org";
    private admin: boolean = false;
    private credentials: Array<string> = [];

    private expire: number = 0;

    private token: string = "";
    private tokenData: any = {};
    public loggedIn: boolean = false;

    public static fromToken(token: string): User {
        try {
            let user = new User();
            user.setToken(token);

            return user;
        } catch (error) {
            console.error("Invalid token", token);
            throw error;
        }
    }

    public static CLAIM_EXPIRE = "exp";
    public static CLAIM_FIRST_NAME = "given_name";
    public static CLAIM_LAST_NAME = "family_name";
    public static CLAIM_EMAIL = "email";
    public static CLAIM_FULL_NAME = "name";
    public static CLAIM_IS_ADMIN = "is_admin";
    public static CLAIM_CREDENTIALS_LIST = "credentials_list";

    private static anonymous: User;

    public static ANONYMOUS(): User {
        if (User.anonymous == null) {
            let anonymous = new User();
            anonymous.setFirstName("Anonymous");
            anonymous.setLastName("");
            anonymous.setEmail("anonymous@opensilex.org");

            User.anonymous = anonymous;
        }

        return User.anonymous;
    }

    public static logout(): User {
        $cookies.remove(User.COOKIE_NAME);

        return User.ANONYMOUS();
    }

    private static COOKIE_NAME = "opensilex-token";

    public static fromCookie(): User {
        let token = $cookies.get(User.COOKIE_NAME);
        let user: User = User.ANONYMOUS();
        if (token != null) {
            try {
                user = User.fromToken(token);
            } catch (error) {
                console.error(error);
            }
        }

        return user;
    }

    public getExpirationMs() {
        return (this.expire * 1000 - Date.now());
    }

    public getInactivityRenewDelayMs() {
        let exipreAfter = this.getExpirationMs();
        return Math.floor((2 / 3) * exipreAfter);
    }

    public needRenew() {
        return this.getExpirationMs() > 0 && this.getInactivityRenewDelayMs() <= 0;
    }

    public getToken() {
        return this.token;
    }

    public getAuthorizationHeader() {
        return "Bearer " + this.getToken();
    }


    public setToken(token: string) {
        let secure: boolean = ('https:' == document.location.protocol);

        this.token = token;
        this.tokenData = jwtDecode(token);

        this.firstName = this.getTokenData(User.CLAIM_FIRST_NAME);
        this.lastName = this.getTokenData(User.CLAIM_LAST_NAME);
        this.email = this.getTokenData(User.CLAIM_EMAIL);
        this.admin = this.getTokenData(User.CLAIM_IS_ADMIN);
        this.credentials = this.getTokenData(User.CLAIM_CREDENTIALS_LIST);
        this.loggedIn = true;
        this.expire = parseInt(this.getTokenData(User.CLAIM_EXPIRE));

        $cookies.set(User.COOKIE_NAME, token, this.expire + "s", "/", undefined, secure);
    }

    public getFirstName() {
        return this.firstName;
    }

    public setFirstName(firstName: string) {
        this.firstName = firstName;
    }

    public getLastName() {
        return this.lastName;
    }

    public setLastName(lastName: string) {
        this.lastName = lastName;
    }

    public getEmail() {
        return this.email;
    }

    public setEmail(email: string) {
        this.email = email;
    }

    public isAdmin() {
        return this.admin;
    }

    public setAdmin(admin: boolean) {
        this.admin = admin;
    }

    public isLoggedIn() {
        return this.loggedIn;
    }

    public hasCredentials(...credentials): boolean {
        if (this.isAdmin()) {
            return true;
        }

        for (let i in credentials) {
            let credential = credentials[i];
            if (this.credentials.indexOf(credential) < 0) {
                return false;
            }
        }
        
        return true;
    }

    public getTokenData(key: string) {
        console.debug("Get token data", key, this.tokenData[key], this.tokenData);
        return this.tokenData[key];
    }
}