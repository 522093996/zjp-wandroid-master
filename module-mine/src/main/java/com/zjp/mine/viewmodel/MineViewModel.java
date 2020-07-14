package com.zjp.mine.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.zjp.base.viewmodel.BaseViewModel;
import com.zjp.mine.api.MineService;
import com.zjp.mine.bean.Integral;
import com.zjp.mine.cache.CacheUtil;
import com.zjp.network.bean.BaseResponse;
import com.zjp.network.https.RetrofitHelper;
import com.zjp.network.observer.NetCallback;
import com.zjp.network.observer.NetHelperObserver;
import com.zjp.network.scheduler.IoMainScheduler;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zjp on 2020/07/07 15:55
 */
public class MineViewModel extends BaseViewModel {

    public MutableLiveData<Integral> mIntegralLiveData = new MutableLiveData<>();
    public MutableLiveData<String> mCacheSizeLiveData = new MutableLiveData<>();

    public MineViewModel(@NonNull Application application) {
        super(application);
    }

    public void getIntegral() {
        RetrofitHelper.getInstance().create(MineService.class)
                .getIntegral()
                .compose(new IoMainScheduler<>())
                .doOnSubscribe(this)
                .subscribe(new NetHelperObserver<>(new NetCallback<BaseResponse<Integral>>() {
                    @Override
                    public void success(BaseResponse<Integral> response) {
                        mIntegralLiveData.postValue(response.getData());
                    }

                    @Override
                    public void error(String msg) {

                    }
                }));
    }

    public void getCacheSize() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String cacheSize = CacheUtil.getTotalCacheSize();
                if (!emitter.isDisposed()) {
                    emitter.onNext(cacheSize);
                }
            }
        }).compose(new IoMainScheduler<>())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        mCacheSizeLiveData.postValue(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
