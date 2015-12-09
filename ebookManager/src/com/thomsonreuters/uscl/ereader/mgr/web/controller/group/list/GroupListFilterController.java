package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupForm.DisplayGroupSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupListFilterForm.FilterCommand;

@Controller
public class GroupListFilterController extends AbstractGroupController{
	
	private Validator validator;
	
	@InitBinder(GroupListFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.setValidator(validator);
	}
	
	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_GROUP_LIST_FILTER_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(HttpSession httpSession,
						@ModelAttribute(GroupListFilterForm.FORM_NAME) @Valid GroupListFilterForm filterForm,
						BindingResult errors,
						Model model) throws Exception {
		// Restore state of paging and sorting
		PageAndSort<DisplayGroupSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		GroupForm groupForm = new GroupForm();
		groupForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());
		
		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
			filterForm.initialize();
		}
		
		pageAndSort.setPageNumber(1);

		setUpModel(filterForm, pageAndSort, httpSession, model);
		model.addAttribute(GroupForm.FORM_NAME, groupForm);

		return new ModelAndView(WebConstants.VIEW_BOOK_GROUP_LIST);
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
