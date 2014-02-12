/**
 * Application main view model.
 */

define(['ko', 'SearchViewModel','sammy'],
    function(ko, searchViewModel, sammy) {

    console.log("defining main view model");


    return function AppViewModel() {
        var self = this;

        self.searchViewModel = ko.observable(null);

        self.resetMembers = function() {
            self.searchViewModel(null);
        }

        // Client-side routes
        sammy(function() {

            this.get('#search', function() {
                self.resetMembers();
                self.searchViewModel(new searchViewModel());
            });

            this.get('', function() {
                this.app.runRoute('get', '#search')
            });

        }).run();


    }


});

