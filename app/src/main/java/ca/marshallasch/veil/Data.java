package ca.marshallasch.veil;

/**
 * @author Weihan
 * @since 2018-06-04
 *
 * Description:
 * This class holds all the data fetching functions
 * TODO: Change this false data into real data fetching functions
 */


public class Data {
    private static String[] forumTitles = {"Nose Discharge",
            "Arm Swelling",
            "Face Swelling",
            "Is it Mumps?",
            "Where to Get Vaccinations"
    };

    private static String[] content = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            "Senectus et netus et malesuada fames ac turpis egestas. Justo nec ultrices dui sapien eget mi proin sed. Massa id neque aliquam vestibulum morbi blandit cursus. Viverra mauris in aliquam sem. Elementum pulvinar etiam non quam lacus suspendisse faucibus interdum. Viverra vitae congue eu consequat ac felis donec. Purus sit amet volutpat consequat mauris nunc congue. Tellus orci ac auctor augue mauris augue. Ipsum faucibus vitae aliquet nec ullamcorper sit. Sed blandit libero volutpat sed cras. Nunc consequat interdum varius sit amet mattis vulputate. Quis ipsum suspendisse ultrices gravida dictum fusce ut placerat. Vel pharetra vel turpis nunc eget.",
            "Non pulvinar neque laoreet suspendisse interdum consectetur libero id faucibus. Ipsum suspendisse ultrices gravida dictum fusce ut. Orci dapibus ultrices in iaculis nunc sed. Dui nunc mattis enim ut tellus elementum. Vel eros donec ac odio tempor orci. Ipsum dolor sit amet consectetur adipiscing elit pellentesque. Et odio pellentesque diam volutpat commodo sed. Duis convallis convallis tellus id interdum velit laoreet. Accumsan lacus vel facilisis volutpat est velit egestas. Pellentesque habitant morbi tristique senectus et netus et malesuada fames. Eget egestas purus viverra accumsan in nisl. Sit amet risus nullam eget felis.",
            "Tristique senectus et netus et. Consequat semper viverra nam libero justo laoreet sit. Purus gravida quis blandit turpis cursus. Vitae turpis massa sed elementum tempus egestas. Egestas fringilla phasellus faucibus scelerisque eleifend donec. Lectus nulla at volutpat diam ut venenatis tellus in. Pellentesque adipiscing commodo elit at imperdiet dui accumsan sit. Auctor neque vitae tempus quam pellentesque nec. Mi tempus imperdiet nulla malesuada pellentesque elit eget. Arcu cursus euismod quis viverra nibh cras pulvinar. Fermentum iaculis eu non diam phasellus vestibulum. Nunc eget lorem dolor sed viverra ipsum nunc. Aliquet nibh praesent tristique magna sit amet. Iaculis nunc sed augue lacus viverra. Sed arcu non odio euismod lacinia. Sagittis purus sit amet volutpat consequat mauris nunc congue nisi. Augue lacus viverra vitae congue eu consequat. Ac feugiat sed lectus vestibulum mattis ullamcorper. Fusce id velit ut tortor pretium viverra. Vestibulum morbi blandit cursus risus at ultrices.",
            "Ipsum my lorem my dude."
    };

    public static String[] getContent(){
        return content;
    }

    public static String[] getTitles(){
        return forumTitles;
    }


}
