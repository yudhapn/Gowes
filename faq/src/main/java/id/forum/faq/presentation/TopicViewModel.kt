package id.forum.faq.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.faq.domain.model.Topic
import id.forum.faq.domain.usecase.GetFaqsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
internal class TopicViewModel(
    private val getFaqsUseCase: GetFaqsUseCase
) :
    BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _topics = MutableLiveData<Resource<List<Topic>>>()
    val topics: LiveData<Resource<List<Topic>>>
        get() = _topics

    init {
        getFaqs()
    }

    private fun getFaqs() {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getFaqsUseCase.execute()
                .onStart {
                    _topics.postValue(Resource.loading(_topics.value?.data))
                }
                .catch { cause ->
                    _topics.postValue(Resource.error(cause.message.toString(), _topics.value?.data))
                }
                .collect { topics ->
                    _topics.postValue(Resource.success(topics))
                }
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(TopicViewModel::class.java.simpleName, "An error happened: $e")
    }
}