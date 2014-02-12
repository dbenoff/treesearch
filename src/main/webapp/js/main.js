require(['ko',
    'AppViewModel',
    'infuser',
    'koExternalTemplateEngine',
    'libs/bootstrap',
    'sammy',
    'jquery',
    'jqueryui'
],
    function (ko, vm, infuser) {


        console.log("main()");

        infuser.defaults.templateUrl = "templates";

        // 'created' binding
        ko.bindingHandlers.created = {
            init: function(element, valueAccessor){
                if (typeof valueAccessor() === 'function'){
                    valueAccessor()(element);
                }
            }
        };

        // 'destroyed' binding
        ko.bindingHandlers.destroyed = {
            init:function(element, valueAccessor){
                if(typeof valueAccessor() === 'function') ko.utils.domNodeDisposal.addDisposeCallback(element, valueAccessor());
            }
        };

        ko.applyBindings(new vm());
    });


