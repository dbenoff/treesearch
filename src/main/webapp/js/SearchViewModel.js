define(['ko'], function(ko) {

    console.log("defining SearchViewModel");

    return function SearchViewModel() {

        var self = this;
        self.searchTerm = ko.observable();
        self.searchResult= ko.observable();
        self.page = ko.observable(0);
        self.searchResults = ko.observableArray([]);

        var typingTimer;
        var doneTypingInterval = 700;

        self.searchTerm.subscribe(function () {
            clearTimeout(typingTimer);
            typingTimer = setTimeout(
                function(){self.search();},
                doneTypingInterval
            );
        });

        self.search = function() {
            self.page(0); //search term changed, reset the page
            $.getJSON( "/api/search/property/" + self.searchTerm() + '/' + self.page(), function( data ) {
                self.searchResults(data.properties);
                self.searchResult(data);
            });
        };

    };
});
