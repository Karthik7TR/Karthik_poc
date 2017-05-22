<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<script src="js/jsgrid/db.js"></script>
<script src="js/jsgrid/src/jsgrid.core.js"></script>
<script src="js/jsgrid/src/jsgrid.load-indicator.js"></script>
<script src="js/jsgrid/src/jsgrid.load-strategies.js"></script>
<script src="js/jsgrid/src/jsgrid.sort-strategies.js"></script>
<script src="js/jsgrid/src/jsgrid.field.js"></script>
<script src="js/jsgrid/src/fields/jsgrid.field.text.js"></script>
<script src="js/jsgrid/src/fields/jsgrid.field.control.js"></script>
<script src="js/jsgrid/src/jsgrid.validation.js"></script>
    
<style>
   .hasDatepicker {
       width: 100px;
       text-align: center;
   }
   .ui-datepicker * {
       font-family: 'Helvetica Neue Light', 'Open Sans', Helvetica;
       font-size: 14px;
       font-weight: 300 !important;
   }
   #jsGrid input {
        width: 100%;
   }
   #jsGrid input[type='button'] {
        width: 0;
        padding-right:10px;
   }
</style>
    
<div id="jsGrid"></div>

<input type='hidden' id='printComponents' name='printComponents' value='${form.printComponents}'>

<script>
    $(function() {
        $("#jsGrid").jsGrid({
            width: "100%",
            autoload: true,
            deleteConfirm: "Do you really want to delete the client?",
            editing: ${param.edit},
            inserting: ${param.edit},
            paging: true,
            
            rowClass: function(item, itemIndex) {
                return "client-" + itemIndex;
            },
            
            controller: {
                init : function() {
                    this.printComponents = $.parseJSON($('#printComponents').val());
                    if ($("#editBookDefinitionForm").length > 0) {
                        $(document).on('submit','#editBookDefinitionForm', function() {
                            $('#printComponents').attr('value', JSON.stringify($("#jsGrid").data("JSGrid").data));
                        });
                    }
                },
                loadData: function() {
                    this.init();
                    return this.printComponents.slice(0, 15);
                }
            },
            fields: [
                { name: "componentOrder", title: "Order", type: "text", width: 40, sorter: "numberAsString", validate: "required"  },
                { name: "materialNumber", title: "Material Number", type: "text", width: 100,
                    validate: [
                               "required",
                               { 
                            	   message: "Only numbers are allowed for Material",
                            	   validator: function(value, item) {
                                   return !isNaN(value) ;
                               }
                               }
                           ]
                },
                { name: "componentName", title: "Component Name", type: "text", width: 80, validate: "required"},
                { type: "control", editButton: false, modeSwitchButton: false, deleteButton: true, visible: ${param.edit} }
            ],
            onRefreshed: function() {
                var $gridData = $("#jsGrid .jsgrid-grid-body tbody");
     
                $gridData.sortable({
                    update: function(e, ui) {
                        // array of indexes
                        var clientIndexRegExp = /\s*client-(\d+)\s*/;
                        var indexes = $.map($gridData.sortable("toArray", { attribute: "class" }), function(classes) {
                            return clientIndexRegExp.exec(classes)[1];
                        });
                        // arrays of items
                        var items = $.map($gridData.find("tr"), function(row) {
                            return $(row).data("JSGridItem");
                        });
                        console && console.log("Reordered items", items);
                    }
                });
            }
        });
        
        $("#jsGrid").jsGrid("sort", { field: "componentOrder", order: "asc" });
    });
</script>
